package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            if (line == null) {
                return;
            }

            String[] request = line.split(" ");
            String httpMethod = request[0];
            String uri = request[1];
            String protocol = request[2];

            final var contentType = parseContentType(uri);

            if (httpMethod.equals("GET") && uri.startsWith("/login")) {

                int index = uri.indexOf("?");
                String path = uri.substring(0, index);
                Map<String, String> queryString = parseQueryString(uri.substring(index + 1));

                final var body = getResponseBody(path);

                Optional<User> user = InMemoryUserRepository.findByAccount(queryString.get("account"));
                if (user.isPresent() && user.get().checkPassword(queryString.get("password"))) {
                    log.info("user: {}", user.get());
                }

                final var response = createHttpResponse(body, contentType);
                outputStream.write(response.getBytes());
                outputStream.flush();
            } else if (httpMethod.equals("GET")) {
                final var body = getResponseBody(uri);

                final var response = createHttpResponse(body, contentType);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        String[] parameters = queryString.split("&");
        Map<String, String> queryParameters = new HashMap<>();

        Arrays.stream(parameters)
                .forEach(parameter -> {
                            String[] split = parameter.split("=");
                            queryParameters.put(split[0], split[1]);
                        }
                );

        return queryParameters;
    }

    private String getResponseBody(String path) throws IOException {
        URL resource = getResource(path);
        if (resource == null) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL getResource(String path) {
        String targetPath;

        if (path.equals("/")) {
            targetPath = "/index.html";
        } else if (path.equals("/login")) {
            targetPath = "/login.html";
        } else {
            targetPath = path;
        }

        return getClass().getClassLoader().getResource("static" + targetPath);
    }

    private String parseContentType(String uri) {
        if (uri.endsWith(".css")) {
            return "text/css;charset=utf-8";
        } else if (uri.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        } else {
            return "text/html;charset=utf-8";
        }
    }

    private String createHttpResponse(String body, String contentType) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
