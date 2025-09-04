package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final int REQUEST_URL_INDEX = 1;
    private static final int QUERY_KEY_INDEX = 0;
    private static final int QUERY_VALUE_INDEX = 1;

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final BufferedReader httpRequestReader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestURL = parseRequestURL(httpRequestReader.readLine());

            String resource = requestURL.split("\\?")[0];

            if (resource.startsWith("/login")) {
                resource = "/login.html";
                login(requestURL);
            }

            sendResponse(resource, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestURL(final String requestLine) throws IOException {
        return requestLine.split(" ")[REQUEST_URL_INDEX];
    }

    private void login(final String requestURL) {
        if (!requestURL.contains("?")) {
            return;
        }

        final String queryString = requestURL.split("\\?")[1];
        final Map<String, String> queries = Arrays.stream(queryString.split("&"))
                .collect(
                        Collectors.toMap(
                                query -> query.split("=")[QUERY_KEY_INDEX],
                                query -> query.split("=")[QUERY_VALUE_INDEX]
                        )
                );

        final String account = queries.get("account");
        final String password = queries.get("password");
        InMemoryUserRepository.findByAccount(account).ifPresent(
                user -> {
                    if (user.checkPassword(password)) {
                        log.info("user: {}", user);
                    }
                }
        );
    }

    private void sendResponse(final String resource, final OutputStream outputStream) throws IOException {
        final Path resourcePath = getResourcePath(resource);
        final String response = createHttpResponse(resourcePath);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Path getResourcePath(final String requestURL) {
        if (requestURL.equals("/")) {
            return getResourcePath("/index.html");
        }
        final URL resource = getClass().getClassLoader().getResource("static" + requestURL);
        if (resource == null) {
            return getResourcePath("/404.html");
        }
        return Paths.get(resource.getFile());
    }

    private String createHttpResponse(final Path resourcePath) throws IOException {
        final String responseBody = new String(Files.readAllBytes(resourcePath));
        final String contentType = Files.probeContentType(resourcePath);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
