package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> mimeTypes = Map.of("html", "text/html", "css", "text/css", "js",
            "text/javascript");

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final List<String> requestHeader = new ArrayList<>();
            while (reader.ready()) {
                requestHeader.add(reader.readLine());
            }
            final String requestLine = requestHeader.getFirst();
            final String requestUri = requestLine.split(" ")[1];
            final String requestPath = requestUri.split("\\?")[0];
            final String requestQueryString = requestUri.split("\\?")[1];

            String responseBody = "Hello world!";
            String contentType = "text/html;charset=utf-8";

            if (requestPath.equals("/login")) {
                final Map<String, String> parameters = getQueryParameters(requestQueryString);
                final String account = parameters.get("account");
                final String password = parameters.get("password");
                InMemoryUserRepository.findByAccount(account)
                        .ifPresent(user -> {
                            if (user.checkPassword(password)) {
                                log.info("user: " + user);
                            }
                        });

                final URL resource = getResource("static/login.html");
                responseBody = readFile(resource);
                contentType = getMimeType(resource);
            }

            if (!requestPath.equals("/") && !requestPath.equals("/login")) {
                final URL resource = getResource("static" + requestUri);
                responseBody = readFile(resource);
                contentType = getMimeType(resource);
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getQueryParameters(final String queryString) {
        final String[] queryParameters = queryString.split("&");
        return Arrays.stream(queryParameters)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]
                ));
    }

    private URL getResource(final String path) throws FileNotFoundException {
        final URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new FileNotFoundException();
        }
        return resource;
    }

    private String readFile(final URL resource) throws IOException {
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getMimeType(final URL resource) throws IOException {
        final String responseResourceExtension = resource.getPath().split("\\.")[1];
        return mimeTypes.get(responseResourceExtension);
    }
}
