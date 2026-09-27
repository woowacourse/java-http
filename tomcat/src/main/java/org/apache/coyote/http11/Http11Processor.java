package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

            final var request = HttpRequest.read(inputStream);
            final var responseHeaders = new LinkedHashMap<String, String>();
            final var redirectPath = getRedirectPath(request);
            if (redirectPath != null) {
                responseHeaders.put("Location", redirectPath);
                writeResponse(outputStream, "302 Found", "text/html;charset=utf-8", new byte[0], responseHeaders);
                return;
            }

            final var requestPath = request.getPath();
            final var responseBody = getResponseBody(requestPath);
            final var contentType = getContentType(requestPath);
            writeResponse(outputStream, "200 OK", contentType, responseBody, responseHeaders);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRedirectPath(final HttpRequest request) {
        if ("POST".equals(request.getMethod()) && "/login".equals(request.getPath())) {
            return authenticate(request);
        }
        if ("POST".equals(request.getMethod()) && "/register".equals(request.getPath())) {
            return register(request);
        }
        return null;
    }

    private String authenticate(final HttpRequest request) {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
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

    private String register(final HttpRequest request) {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
        final var email = request.getParameter("email");
        if (account == null || account.isBlank() || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            return "/register";
        }
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return "/index.html";
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
        if ("/register".equals(requestPath)) {
            resourcePath = "static/register.html";
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
