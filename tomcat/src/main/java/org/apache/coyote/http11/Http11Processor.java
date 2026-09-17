package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            final String[] tokens = requestLine.split(" ");
            final String uri = tokens[1];

            final int questionMarkIndex = uri.indexOf("?");

            final String path;
            final String queryString;

            final Map<String, String> queryParams = new HashMap<>();

            if (questionMarkIndex != -1) {
                path = uri.substring(0, questionMarkIndex);
                queryString = uri.substring(questionMarkIndex + 1);
            } else {
                path = uri;
                queryString = "";
            }


            if (!queryString.isEmpty()) {
                final String[] parameters = queryString.split("&");
                for (String parameter : parameters) {
                    final String[] keyValue = parameter.split("=", 2);

                    final String key = keyValue[0];
                    final String value = keyValue[1];
                    queryParams.put(key, value);
                }
            }

            final String responseBody;
            final String resourcePath;

            if ("/".equals(path)) {
                responseBody = "Hello world!";
                resourcePath = path;

            } else {
                if ("/login".equals(path)) {
                    resourcePath = "/login.html";

                    if (!queryString.isEmpty()) {
                        final String account = queryParams.get("account");
                        final String password = queryParams.get("password");

                        InMemoryUserRepository.findByAccount(account)
                                .filter(user -> user.checkPassword(password))
                                .ifPresent(user ->
                                        log.info("회원 조회 결과: {}", user)
                                );
                    }

                } else {
                    resourcePath = path;
                }

                final ClassLoader classLoader = getClass().getClassLoader();
                final URL resource =
                        classLoader.getResource("static" + resourcePath);

                if (resource == null) {
                    responseBody = "";
                } else {
                    final URI fileUri = resource.toURI();
                    final Path filePath = Paths.get(fileUri);
                    final byte[] fileBytes = Files.readAllBytes(filePath);

                    responseBody =
                            new String(fileBytes, StandardCharsets.UTF_8);
                }
            }

            final String contentType;

            if (uri.endsWith(".css")) {
                contentType = "text/css ";
            } else if (uri.endsWith(".js")) {
                contentType = "application/javascript";
            } else {
                contentType = "text/html;charset=utf-8 ";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
