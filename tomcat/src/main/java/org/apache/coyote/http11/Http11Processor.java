package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

            final var requestUri = getRequestUri(inputStream);
            final var requestPath = getRequestPath(requestUri);
            final var parameters = parseQueryString(requestUri);
            authenticate(requestPath, parameters);

            final var responseBody = getResponseBody(requestPath);
            final var contentType = getContentType(requestPath);
            writeResponse(outputStream, contentType, responseBody);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestUri(final InputStream inputStream) throws IOException {
        final var requestLine = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        ).readLine();
        if (requestLine == null) {
            return "/";
        }
        return requestLine.split(" ")[1];
    }

    private String getRequestPath(final String requestUri) {
        final var queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex >= 0) {
            return requestUri.substring(0, queryStringIndex);
        }
        return requestUri;
    }

    private Map<String, String> parseQueryString(final String requestUri) {
        final var parameters = new HashMap<String, String>();
        final var queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex < 0) {
            return parameters;
        }

        final var queryString = requestUri.substring(queryStringIndex + 1);
        if (!queryString.isEmpty()) {
            final var queryParameters = queryString.split("&");
            for (final var queryParameter : queryParameters) {
                final var keyValue = queryParameter.split("=", 2);
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }

        return parameters;
    }

    private void authenticate(final String requestPath, final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        if (!"/login".equals(requestPath) || account == null || password == null) {
            return;
        }

        final var foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isPresent()) {
            final var user = foundUser.get();
            if (user.checkPassword(password)) {
                log.info("login user: {}", user);
            }
        }
    }

    private byte[] getResponseBody(final String requestPath) throws IOException {
        var responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
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
                responseBody = resource.readAllBytes();
            }
        }
        return responseBody;
    }

    private String getContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (requestPath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private void writeResponse(final OutputStream outputStream, final String contentType, final byte[] responseBody)
            throws IOException {
        final var responseHeaders = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");

        outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
