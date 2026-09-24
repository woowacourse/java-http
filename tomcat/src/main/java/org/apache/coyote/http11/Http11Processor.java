package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

            final var bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }

            final String method = requestLine.split(" ")[0];
            final String requestUri = requestLine.split(" ")[1];
            final String requestPath = requestPath(requestUri);

            final Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
                final int colonIndex = headerLine.indexOf(":");
                if (colonIndex > 0) {
                    headers.put(headerLine.substring(0, colonIndex).toLowerCase(),
                            headerLine.substring(colonIndex + 1).trim());
                }
            }

            String requestBody = "";
            if ("POST".equals(method)) {
                final int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
                final char[] buffer = new char[contentLength];
                int readCount = 0;
                while (readCount < contentLength) {
                    final int count = bufferedReader.read(buffer, readCount, contentLength - readCount);
                    if (count == -1) {
                        throw new IOException("Request body ended early");
                    }
                    readCount += count;
                }
                requestBody = new String(buffer);
            }

            if ("POST".equals(method) && "/register".equals(requestPath)) {
                final Map<String, String> parameters = queryParameters(requestBody);
                final String account = parameters.get("account");
                final String password = parameters.get("password");
                final String email = parameters.get("email");
                if (account == null || password == null || email == null) {
                    final String response = "HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n\r\n";
                    outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                    return;
                }
                InMemoryUserRepository.save(new User(account, password, email));
                sendRedirect(outputStream, "/index.html");
                return;
            }

            if ("POST".equals(method) && "/login".equals(requestPath)) {
                final Map<String, String> loginParameters = queryParameters(requestBody);
                final boolean logInIsSuccess = logIn(loginParameters);
                final String location = logInIsSuccess ? "/index.html" : "/401.html";
                sendRedirect(outputStream, location);
                return;
            }

            final String responseBody = responseBody(requestPath);
            final String contentType = contentType(requestPath);

            final String response = "HTTP/1.1 200 OK \r\n"
                    + "Content-Type: " + contentType + " \r\n"
                    + "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " \r\n\r\n"
                    + responseBody;

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }

    }

    private void sendRedirect(final OutputStream outputStream, final String location) throws IOException {
        final String response = "HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n\r\n";
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String requestPath(final String requestUri) {
        final int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex < 0) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }

    private boolean logIn(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        if (account == null || password == null) {
            return false;
        }

        final var user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return false;
        }
        log.info("login user: {}", account);
        return true;
    }

    private Map<String, String> queryParameters(final String queryString) {
        final Map<String, String> queryParameters = new HashMap<>();
        for (String parameter : queryString.split("&")) {
            final String[] nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length == 2) {
                queryParameters.put(URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8));
            }
        }
        return queryParameters;
    }

    private String responseBody(final String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return "Hello world!";
        }

        final String resourcePath;
        if ("/login".equals(requestPath) || "/register".equals(requestPath)) {
            resourcePath = requestPath + ".html";
        } else {
            resourcePath = requestPath;
        }
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            return "";
        }

        return Files.readString(Path.of(resource.getPath()), StandardCharsets.UTF_8);
    }

    private String contentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
