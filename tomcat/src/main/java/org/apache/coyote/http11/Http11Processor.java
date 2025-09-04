package org.apache.coyote.http11;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
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

            final var httpRequestMessage = readMessage(inputStream);
            final var response = createResponse(httpRequestMessage);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readMessage(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var stringBuilder = new StringBuilder();

        while (true) {
            final var line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            stringBuilder.append(line).append("\r\n");
        }
        return stringBuilder.toString();
    }

    private String createResponse(final String httpRequestMessage) throws IOException {
        if (httpRequestMessage.startsWith("GET /index.html HTTP/1.1")) {
            final var resource = getClass().getClassLoader().getResource("static/index.html");

            final var body = Files.readString(Path.of(resource.getPath()));
            final var header = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " "
            );
            return header + "\r\n\r\n" + body;
        }

        if (httpRequestMessage.startsWith("GET /css/styles.css HTTP/1.1")) {
            final var resource = getClass().getClassLoader().getResource("static/css/styles.css");

            final var body = Files.readString(Path.of(resource.getPath()));
            final var header = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " "
            );
            return header + "\r\n\r\n" + body;
        }

        final var indexOfHttpVersion = httpRequestMessage.indexOf("HTTP/1.1");
        final var resourcePath = httpRequestMessage.substring(4, indexOfHttpVersion - 1);
        if (resourcePath.endsWith(".js")) {
            final var resource = getClass().getClassLoader().getResource("static/" + resourcePath);

            final var body = Files.readString(Path.of(resource.getPath()));
            final var header = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: application/javascript;charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " "
            );
            return header + "\r\n\r\n" + body;
        }

        if (resourcePath.startsWith("/login")) {
            final var resource = getClass().getClassLoader().getResource("static/login.html");

            if (resourcePath.charAt(6) == '?') {
                final var queryParams = extractQueryParams(resourcePath);
                if (queryParams.containsKey("account") && queryParams.containsKey("password")) {
                    tryLogin(queryParams);
                }
            }
            final var body = Files.readString(Path.of(resource.getPath()));
            final var header = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " "
            );
            return header + "\r\n\r\n" + body;
        }

        final var body = "Hello world!";
        final var header = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " "
        );
        return header + "\r\n\r\n" + body;
    }

    private void tryLogin(final Map<String, String> queryParams) {
        final var account = queryParams.get("account");
        final var password = queryParams.get("password");
        InMemoryUserRepository.findByAccount(account)
                .filter(u -> u.checkPassword(password))
                .ifPresent(u -> log.info("user : {}", u));
    }

    private Map<String, String> extractQueryParams(final String resourcePath) {
        final var queryParams = resourcePath.substring(7);
        return Arrays.stream(queryParams.split("&"))
                .map(param -> param.split("="))
                .filter(param -> param.length == 2)
                .collect(toMap(
                        param -> param[0],
                        param -> param[1])
                );
    }
}
