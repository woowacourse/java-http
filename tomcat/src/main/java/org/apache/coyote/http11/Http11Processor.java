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
import java.util.LinkedHashMap;
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
            final var responseHeaders = new LinkedHashMap<String, String>();
            final var redirectPath = authenticate(requestPath, parameters);
            if (redirectPath != null) {
                responseHeaders.put("Location", redirectPath);
                writeResponse(outputStream, "302 Found", "text/html;charset=utf-8", new byte[0], responseHeaders);
                return;
            }

            final var responseBody = getResponseBody(requestPath);
            final var contentType = getContentType(requestPath);
            writeResponse(outputStream, "200 OK", contentType, responseBody, responseHeaders);
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

    private String authenticate(final String requestPath, final Map<String, String> parameters) {
        if (!"/login".equals(requestPath) || parameters.isEmpty()) {
            return null;
        }
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        if (account == null || password == null) {
            return "/401.html";
        }

        final var foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isPresent()) {
            final var user = foundUser.get();
            if (user.checkPassword(password)) {
                return "/index.html";
            }
        }
        return "/401.html";
    }

    private byte[] getResponseBody(final String requestPath) throws IOException {
        var responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
        var resourcePath = "";

        if ("/index.html".equals(requestPath)
                || "/401.html".equals(requestPath)
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

    private void writeResponse(final OutputStream outputStream, final String status, final String contentType,
                               final byte[] responseBody, final Map<String, String> headers) throws IOException {
        final var responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 ").append(status).append("\r\n");
        responseHeaders.append("Content-Type: ").append(contentType).append("\r\n");
        responseHeaders.append("Content-Length: ").append(responseBody.length).append("\r\n");
        for (final var header : headers.entrySet()) {
            responseHeaders.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        responseHeaders.append("\r\n");

        outputStream.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
