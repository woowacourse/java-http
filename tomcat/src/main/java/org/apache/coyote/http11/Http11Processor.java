package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = SessionManager.getInstance();

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
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String[] requestMessage = extractRequestMessage(bufferedReader);
            Map<String, String> requestHeaders = extractRequestHeaders(bufferedReader);

            String body = extractRequestBody(bufferedReader, requestHeaders);

            String method = requestMessage[0];
            String requestTarget = requestMessage[1];
            String requestPath = extractRequestPath(requestTarget);
            Map<String, String> queryParameters = parseQueryParameters(requestTarget);

            Optional<HttpResponse> handledResponse = dispatchRequest(
                    method,
                    requestPath,
                    queryParameters,
                    body,
                    requestHeaders
            );
            if (handledResponse.isPresent()) {
                outputStream.write(handledResponse.get().toString().getBytes());
                outputStream.flush();
                return;
            }

            String resourcePath = resolveResourcePath(requestPath);
            String responseBody = resolveResponseBody(resourcePath);
            if (responseBody == null) {
                HttpResponse httpResponse = HttpResponse.createNotFoundResponse(
                        readNotFoundPage()
                );
                outputStream.write(httpResponse.toString().getBytes());
                outputStream.flush();
            } else {
                String contentType = resolveContentType(resourcePath);
                HttpResponse httpResponse = HttpResponse.createOkResponse(
                        contentType,
                        responseBody,
                        Map.of()
                );
                outputStream.write(httpResponse.toString().getBytes());
                outputStream.flush();
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        final String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return "";
        }
        final char[] buffer = new char[Integer.parseInt(contentLength)];
        bufferedReader.read(buffer);
        return new String(buffer);
    }

    private String readNotFoundPage() throws IOException {
        URL resourceUrl = getClass().getClassLoader().getResource("static/404.html");
        return readStaticResource(resourceUrl);
    }

    private Optional<HttpResponse> dispatchRequest(
            String method,
            String requestPath,
            Map<String, String> queryParameters,
            String body,
            Map<String, String> requestHeaders) {

        if ("GET".equals(method) && "/login".equals(requestPath)) {
            return handleLoginRequest(queryParameters, requestHeaders);
        }
        if ("POST".equals(method) && "/register".equals(requestPath)) {
            return Optional.of(HttpResponse.createRedirectResponse(handleRegister(body), Map.of()));
        }
        return Optional.empty();
    }

    private String handleRegister(String body) {
        final Map<String, String> params = parseParams(body);
        User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email")
        );
        InMemoryUserRepository.save(user);
        return "/index.html";
    }

    private Map<String, String> parseParams(String body) {
        String[] parameterPairs = body.split("&");

        return Arrays.stream(parameterPairs)
                .map(parameterPair -> parameterPair.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private Optional<HttpResponse> handleLoginRequest(
            Map<String, String> queryParameters,
            Map<String, String> requestHeaders) {
        HttpCookie cookie = new HttpCookie(requestHeaders.get("Cookie"));
        String sessionId = cookie.get("JSESSIONID");

        Session session = sessionManager.findSession(sessionId);
        if (session != null && getUser(session) != null) {
            return Optional.of(HttpResponse.createRedirectResponse("/index.html", Map.of()));
        }
        if (queryParameters.isEmpty()) {
            return Optional.empty();
        }

        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        Optional<User> user = login(account, password);
        if (user.isEmpty()) {
            return Optional.of(HttpResponse.createRedirectResponse("/401.html", Map.of()));
        }

        Map<String, String> responseHeaders = new LinkedHashMap<>();

        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            responseHeaders.put("Set-Cookie", "JSESSIONID=" + sessionId);
        }

        Session loginSession = new Session(sessionId);
        loginSession.setAttribute("user", user.get());
        sessionManager.add(loginSession);
        return Optional.of(HttpResponse.createRedirectResponse("/index.html", responseHeaders));
    }

    private Optional<User> login(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));
        user.ifPresent(foundUser -> log.info("user={}", foundUser));
        return user;
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private String extractRequestPath(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf('?');

        if (queryStartIndex < 0) {
            queryStartIndex = requestTarget.length();
        }

        return requestTarget.substring(0, queryStartIndex);
    }

    private Map<String, String> parseQueryParameters(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf('?');

        if (queryStartIndex < 0) {
            return Map.of();
        }

        String queryString = requestTarget.substring(queryStartIndex + 1);
        String[] parameterPairs = queryString.split("&");

        return Arrays.stream(parameterPairs)
                .map(parameterPair -> parameterPair.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private String resolveResourcePath(String requestPath) {
        if ("/".equals(requestPath)) {
            return "/";
        }
        if (!requestPath.contains(".")) {
            requestPath = requestPath + ".html";
        }
        return "static" + requestPath;
    }

    private String[] extractRequestMessage(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();

        return requestLine.split(" ");
    }

    private String resolveResponseBody(String resourcePath) throws IOException {
        if ("/".equals(resourcePath)) {
            return "Hello world!";
        }
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return null;
        }
        return readStaticResource(resourceUrl);
    }

    private String readStaticResource(URL resourceUrl) throws IOException {
        Path filePath = new File(resourceUrl.getPath()).toPath();
        return Files.readString(filePath);
    }

    private String resolveContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private Map<String, String> extractRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();

        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
            String[] parts = headerLine.split(":", 2);
            requestHeaders.put(parts[0], parts[1].trim());
        }

        return requestHeaders;
    }
}
