package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
            final String requestURL = parseRequestURL(inputStream);

            String resource = requestURL.split("\\?")[0];

            if (resource.equals("/")) {
                send200Response("/index.html", outputStream);
                return;
            }
            if (resource.startsWith("/login")) {
                login(requestURL);
                send200Response("/login.html", outputStream);
                return;
            }
            if (resource.startsWith("/index.html")) {
                send200Response("/index.html", outputStream);
                return;
            }
            send200Response(resource, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestURL(final InputStream inputStream) throws IOException {
        final BufferedReader httpRequestReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            final String requestLine = httpRequestReader.readLine();
            return requestLine.split(" ")[REQUEST_URL_INDEX];
        } catch (final NullPointerException | ArrayIndexOutOfBoundsException exception) {
            throw new IOException("Request Line을 읽어올 수 없습니다.");
        }
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

    private void send200Response(final String resource, final OutputStream outputStream) throws IOException {
        try {
            final URL resourceUrl = validateResource(resource);
            final Path resourcePath = Paths.get(resourceUrl.getFile());
            final String response = create200HttpResponse(resourcePath);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final FileNotFoundException e) {
            send404Response(outputStream);
            throw e;
        }
    }

    private void send404Response(final OutputStream outputStream) throws IOException {
        final String response = create404HttpResponse();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private URL validateResource(final String resource) throws FileNotFoundException {
        final URL resourceUrl = getClass().getClassLoader().getResource("static" + resource);
        if (resourceUrl == null) {
            throw new FileNotFoundException("요청한 리소스를 찾을 수 없습니다.");
        }
        return resourceUrl;
    }

    private String create200HttpResponse(final Path resourcePath) throws IOException {
        final String responseBody = new String(Files.readAllBytes(resourcePath));
        final String contentType = Files.probeContentType(resourcePath);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String create404HttpResponse() throws IOException {
        String responseBody;
        String contentType;

        try {
            final URL resourceUrl = validateResource("/404.html");
            final Path resourcePath = Paths.get(resourceUrl.getFile());
            responseBody = new String(Files.readAllBytes(resourcePath));
            contentType = Files.probeContentType(resourcePath);
        } catch (final FileNotFoundException e) {
            responseBody = "<html><body><h1>404 Not Found</h1><p>요청하신 페이지를 찾을 수 없습니다.</p></body></html>";
            contentType = "text/html";
        }

        return String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
