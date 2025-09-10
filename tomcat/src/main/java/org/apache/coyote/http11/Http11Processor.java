package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

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

            final Http11Request request;
            try {
                request = new Http11Request(inputStream);
            } catch (Http11ParseException e) {
                sendErrorResponse(outputStream);
                return;
            }
            final Http11Response response = new Http11Response();

            final String path = extractPath(request.getUri());
            final Http11Method method = request.getMethod();

            String statusLine = "HTTP/1.1 200 OK";
            String responseBody = "Hello world!";
            final Map<String, String> responseHeaders = new LinkedHashMap<>();
            responseHeaders.put("Content-Type", MediaType.detectMimeType(path));

            if ("/logout".equals(path)) {
                statusLine = handleLogout(request, responseHeaders);
                responseBody = "";
            } else if (Http11Method.GET.equals(method)) {
                Entry<String, String> getResult = handleGetRequest(path, request, responseHeaders);
                statusLine = getResult.getKey();
                responseBody = getResult.getValue();
            } else if (Http11Method.POST.equals(method)) {
                statusLine = handlePostRequest(path, request, responseHeaders);
                responseBody = "";
            }

            responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
            response.putStatusLine(statusLine);
            response.putHeaders(responseHeaders);
            response.putBody(responseBody);

            outputStream.write(response.buildResponse(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Http11ParseException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void sendErrorResponse(OutputStream outputStream)
            throws IOException {
        String statusLine = "HTTP/1.1 400 Bad Request"; // TODO: 별도의 핸들러로 관리
        String responseBody = readFileFromClasspath("static/400.html");
        final Map<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", MediaType.HTML.getMimeType());
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        final String response = buildResponse(statusLine, responseHeaders, responseBody);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String handleLogout(final Http11Request request, final Map<String, String> responseHeaders) {
        final Http11Cookie cookie = request.getCookie();
        if (cookie.isContainsSessionId()) {
            SessionManager.getInstance().remove(cookie.getSessionId());
        }
        responseHeaders.put("Location", "/index.html");
        responseHeaders.put("Set-Cookie", "JSESSIONID=; Path=/; Max-Age=0");
        return "HTTP/1.1 302 Found";
    }

    private Entry<String, String> handleGetRequest(final String path, final Http11Request request, 
                                                   final Map<String, String> responseHeaders) {
        String statusLine = "HTTP/1.1 200 OK";
        String responseBody;

        if ("/register".equals(path)) {
            responseBody = readFileFromClasspath("static/register.html");
        } else if ("/login".equals(path) || "/login.html".equals(path)) {
            final Http11Cookie cookie = request.getCookie();
            if (cookie.isContainsSessionId() && SessionManager.getInstance().containsSession(cookie.getSessionId())) {
                statusLine = "HTTP/1.1 302 Found";
                responseHeaders.put("Location", "/index.html");
                responseBody = "";
            } else {
                responseBody = readFileFromClasspath("static/login.html");
            }
        } else if (!"/".equals(path)) {
            responseBody = readFileFromClasspath("static" + path);
            if (responseBody.isEmpty()) {
                statusLine = "HTTP/1.1 404 Not Found";
                responseBody = readFileFromClasspath("static/404.html");
            }
        } else {
            responseBody = "Hello world!";
        }

        return new AbstractMap.SimpleEntry<>(statusLine, responseBody);
    }

    private String handlePostRequest(final String path, final Http11Request request, 
                                     final Map<String, String> responseHeaders) {
        final Map<String, String> params = parseRequestBody(request.getBody());

        if ("/register".equals(path)) {
            final User user = new User(params.get("account"), params.get("password"), params.get("email"));
            InMemoryUserRepository.save(user);
            log.info("User saved: {}", user);
            createSessionAndSetCookie(user, request, responseHeaders);
            responseHeaders.put("Location", "/index.html");
            return "HTTP/1.1 302 Found";
        }

        if ("/login".equals(path)) {
            try {
                final User user = InMemoryUserRepository.findByAccount(params.get("account"))
                        .orElseThrow(() -> new IllegalArgumentException("[ERROR] 회원을 찾을 수 없습니다."));

                if (user.checkPassword(params.get("password"))) {
                    createSessionAndSetCookie(user, request, responseHeaders);
                    responseHeaders.put("Location", "/index.html");
                    return "HTTP/1.1 302 Found";
                } else {
                    responseHeaders.put("Location", "/401.html");
                    return "HTTP/1.1 302 Found";
                }
            } catch (IllegalArgumentException e) {
                responseHeaders.put("Location", "/401.html");
                return "HTTP/1.1 302 Found";
            }
        }
        return "HTTP/1.1 404 Not Found";
    }

    private void createSessionAndSetCookie(final User user, final Http11Request request, 
                                           final Map<String, String> responseHeaders) {
        final Http11Cookie cookie = request.getCookie();
        if (cookie.isNotContainsSessionId() || !SessionManager.getInstance().containsSession(cookie.getSessionId())) {
            final String sessionId = UUID.randomUUID().toString();
            final Http11Session session = new Http11Session(sessionId);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);
            // TODO: CookieSecurityConfig를 통한 HttpOnly 기본, Secure/SameSite 설정 전략 등 고려하기
            responseHeaders.put("Set-Cookie", "JSESSIONID=" + sessionId + "; Path=/");
        }
    }

    private String extractPath(final String uri) {
        if (uri == null || uri.isEmpty()) {
            return "/";
        }
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    private Map<String, String> parseRequestBody(final String requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private String readFileFromClasspath(String resourcePath) {
        final InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (input == null) {
            log.error("resource not found: {}", resourcePath);
            return "";
        }
        final StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append(CRLF);
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", resourcePath, e);
            return "";
        }
        return fileContents.toString();
    }

    private String buildResponse(String statusLine, Map<String, String> responseHeaders, String responseBody) {
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine).append(CRLF);
        appendResponseHeaders(responseHeaders, responseBuilder);
        responseBuilder.append(CRLF);
        responseBuilder.append(responseBody);
        return responseBuilder.toString();
    }

    private void appendResponseHeaders(Map<String, String> responseHeaders, StringBuilder responseBuilder) {
        for (Entry<String, String> entry : responseHeaders.entrySet()) {
            responseBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }
    }
}
