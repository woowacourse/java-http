package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

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

            final var requestLine = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).readLine();
            var requestUri = "/";
            var responseBody = "Hello world!";
            var contentType = "text/html;charset=utf-8";

            if (requestLine != null) {
                requestUri = requestLine.split(" ")[1];
            }

            var requestPath = requestUri;
            var queryString = "";
            final var queryStringIndex = requestUri.indexOf("?");

            if (queryStringIndex >= 0) {
                requestPath = requestUri.substring(0, queryStringIndex);
                queryString = requestUri.substring(queryStringIndex + 1);
            }

            var resourcePath = "";
            if ("/index.html".equals(requestPath)
                    || "/css/styles.css".equals(requestPath)
                    || requestPath.startsWith("/js/")
                    || requestPath.startsWith("/assets/")) {
                resourcePath = "static" + requestPath;
            }
            if ("/login".equals(requestPath)) {
                resourcePath = "static/login.html";
            }

            if (!resourcePath.isEmpty()) {
                try (final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                    responseBody = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
                }
            }

            if (requestPath.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            }
            if (requestPath.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8";
            }

            final var parameters = new HashMap<String, String>();
            if (!queryString.isEmpty()) {
                final var queryParameters = queryString.split("&");
                for (final var queryParameter : queryParameters) {
                    final var keyValue = queryParameter.split("=", 2);
                    if (keyValue.length == 2) {
                        parameters.put(keyValue[0], keyValue[1]);
                    }
                }
            }

            final var account = parameters.get("account");
            final var password = parameters.get("password");
            if ("/login".equals(requestPath) && account != null && password != null) {
                final var foundUser = InMemoryUserRepository.findByAccount(account);
                if (foundUser.isPresent()) {
                    final var user = foundUser.get();
                    if (user.checkPassword(password)) {
                        log.info("login user: {}", user);
                    }
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
