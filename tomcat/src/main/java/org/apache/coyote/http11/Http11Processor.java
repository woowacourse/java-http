package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
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
import java.util.Optional;

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            final String[] requestParts = requestLine.split(" ");

            if (requestParts.length != 3) {
                //잘못된 HTTP 요청
                return;
            }

            if (!readHeaders(reader)) {
                return;
            }

            final String requestUri = requestParts[1];

            final int querySeparatorIndex = requestUri.indexOf("?");
            final String requestPath = extractRequestPath(requestUri, querySeparatorIndex);
            final String queryString = extractQueryString(requestUri, querySeparatorIndex);

            final String resourcePath = resolveResourcePath(requestPath);
            handleLogin(resourcePath, queryString);
            writeResourceResponse(outputStream, resourcePath);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean readHeaders(final BufferedReader reader) throws IOException {
        String headerLine;
        while ((headerLine = reader.readLine()) != null) {
            if (headerLine.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String extractRequestPath(final String requestUri, final int querySeparatorIndex) {
        if (querySeparatorIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, querySeparatorIndex);
    }

    private String extractQueryString(final String requestUri, final int querySeparatorIndex) {
        if (querySeparatorIndex == -1) {
            return "";
        }
        return requestUri.substring(querySeparatorIndex + 1);
    }

    private String resolveResourcePath(final String requestPath) {
        if ("/login".equals(requestPath)) {
            return "static/login.html";
        }
        return "static" + requestPath;
    }

    private void handleLogin(final String resourcePath, final String queryString) {
        if (!"static/login.html".equals(resourcePath) || queryString.isEmpty()) {
            return;
        }

        String account = "";
        String password = "";
        for (final String queryParameter : queryString.split("&")) {
            final String[] parameterParts = queryParameter.split("=", 2);
            if (parameterParts.length != 2) {
                continue;
            }

            final String key = parameterParts[0];
            final String value = parameterParts[1];

            if ("account".equals(key)) {
                account = value;
                continue;
            }
            if ("password".equals(key)) {
                password = value;
            }
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
        }
    }

    private void writeResourceResponse(final OutputStream outputStream,
                                       final String resourcePath) throws IOException {
        if ("static/".equals(resourcePath)) {
            final byte[] bodyBytes = "Hello world!".getBytes(StandardCharsets.UTF_8);
            writeResponse(outputStream, "HTTP/1.1 200 OK ",
                    "text/html;charset=utf-8", bodyBytes);
            return;
        }

        try (final InputStream resourceInputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (resourceInputStream == null) {
                final byte[] bodyBytes = "Not Found".getBytes(StandardCharsets.UTF_8);
                writeResponse(outputStream, "HTTP/1.1 404 Not Found ",
                        "text/plain;charset=utf-8", bodyBytes);
                return;
            }

            final byte[] bodyBytes = resourceInputStream.readAllBytes();
            final String contentType = determineContentType(resourcePath);
            writeResponse(outputStream, "HTTP/1.1 200 OK ", contentType, bodyBytes);
        }
    }

    private void writeResponse(final OutputStream outputStream, final String statusLine,
                               final String contentType, final byte[] bodyBytes) throws IOException {
        final String responseHeaders = String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType + " ",
                "Content-Length: " + bodyBytes.length + " ",
                "",
                "");

        outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        outputStream.write(bodyBytes);
        outputStream.flush();
    }

    private String determineContentType(final String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "text/javascript";
        }
        if (resourcePath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (resourcePath.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }
}
