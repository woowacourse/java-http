package org.apache.coyote.http11;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.HttpCookie;
import com.techcourse.model.HttpResponse;
import com.techcourse.model.User;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();
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

            // 첫번째 라인 & 요청 url 구하기
            String requestLine = readLine(inputStream);
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            String requestMethod = requestLine.split(" ")[0];
            String requestTarget = requestLine.split(" ")[1];

            // 리소스 경로 찾기
            String requestPath = requestTarget.split("\\?")[0];

            // 헤더 읽기
            Map<String, List<String>> requestHeaders = readRequestHeaders(inputStream);

            // 바디 읽기
            String requestBody = readRequestBody(inputStream, requestHeaders);
            Map<String, String> formParameters = parseFormParameters(requestBody);

            // 쿼리 파싱
            Map<String, String> queryParameters = parseQueryParameters(requestTarget);

            HttpResponse httpResponse;
            if (requestPath.equals("/")) {
                httpResponse = new HttpResponse(
                        "200 OK ",
                        Map.of("Content-Type", List.of("text/html;charset=utf-8 ")),
                        "Hello world!".getBytes()
                );
            } else if (requestPath.startsWith("/login")) {
                httpResponse = handleLoginRequest(formParameters, requestHeaders, requestMethod);
            } else if (requestPath.startsWith("/register") && requestMethod.equals("POST")) {
                User user = new User(formParameters.get("account"), formParameters.get("password"), formParameters.get("email"));
                InMemoryUserRepository.save(user);

                Session session = getSession(requestHeaders, true);
                session.setAttribute("user", user);
                HttpCookie cookie = new HttpCookie(session.getId());

                httpResponse = createRegisterSuccessResponse(List.of(cookie.toString()));
                log.info("회원가입 성공 : {}", user.toString());
            } else {
                httpResponse = createResourceResponse(requestPath);
            }

        writeHttpResponse(outputStream, httpResponse);
    } catch (IOException | UncheckedServletException e) {
        log.error(e.getMessage(), e);
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    }
}

    private String getSessionId(Map<String, List<String>> requestHeaders) {
        List<String> cookieHeaders = requestHeaders.get("cookie");

        if (cookieHeaders == null) { return null;}

        for (String cookieHeader : cookieHeaders) {
            for (String cookie : cookieHeader.split(";")) {
                String[] parts = cookie.trim().split("=", 2);

                if (parts.length == 2 && parts[0].equals("JSESSIONID")) {
                    return parts[1].trim();
                }
            }
        }

        return null;
    }

    private Session getSession(Map<String, List<String>> requestHeaders, boolean create) {
        String sessionId = getSessionId(requestHeaders);

        if (sessionId != null) {
            Session session = sessionManager.findSession(sessionId);

            if (session != null) {
                return session;
            }
        }

        if (!create) {
            return null;
        }

        Session session = Session.create();
        sessionManager.add(session);
        return session;
    }
    private Map<String, String> parseFormParameters(String requestBody) throws URISyntaxException, IOException {
        Map<String, String> partsMap = new HashMap<>();

        if (requestBody == null || requestBody.isBlank()) {
            return partsMap;
        }

        String[] parts = requestBody.split("&");
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            String encodedKey = URLDecoder.decode(key, "UTF-8");
            String encodedValue = URLDecoder.decode(value, "UTF-8");

            partsMap.put(encodedKey, encodedValue);
        }
        return partsMap;
    }

    private String readLine(InputStream inputStream) throws IOException {
        StringBuilder line = new StringBuilder();

        int current;
        boolean carriageReturn = false;

        while ((current = inputStream.read()) != -1) {
            if (current == '\r') {
                carriageReturn = true;
                continue;
            }

            if (carriageReturn && current == '\n') {
                break;
            }

            if (carriageReturn) {
                line.append('\r');
                carriageReturn = false;
            }

            line.append((char) current);
        }

        if (current == -1 && line.isEmpty()) {
            return null;
        }

        return line.toString();
    }
    private void writeHttpResponse(OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        byte[] body = httpResponse.body();
        StringBuilder header = new StringBuilder();

        header.append("HTTP/1.1 ")
                .append(httpResponse.statusCode())
                .append("\r\n");

        for (Map.Entry<String, List<String>> entry : httpResponse.headers().entrySet()) {

            String headerName = entry.getKey();

            for (String value : entry.getValue()) {
                header.append(headerName)
                        .append(": ")
                        .append(value)
                        .append("\r\n");
            }
        }

        header.append("Content-Length: ")
                .append(body.length)
                .append(" ")
                .append("\r\n\r\n");

        outputStream.write(header.toString().getBytes());
        outputStream.write(body);

    }

    private HttpResponse handleLoginRequest(Map<String, String> formParameters, Map<String, List<String>> requestHeaders, String requestMethod) throws URISyntaxException, IOException {

        // 로그인 페이지 접근
        if (requestMethod.equals("GET")) {
            Session session = getSession(requestHeaders, false);

            if (session != null && session.getAttribute("user") != null) {
                return createRedirectResponse("/index.html");
            }

            return createResourceResponse("/login");
        }

        // 로그인 제출
        String account = formParameters.get("account");
        String password = formParameters.get("password");

        if (account == null || password == null) {
            return createUnauthorizedResponse();
        }

        final var user = InMemoryUserRepository
                .findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password))
                .orElse(null);

        if  (user == null) {
            return createUnauthorizedResponse();
        }

        // 성공한 경우에만 세션 생성
        Session session = getSession(requestHeaders, true);
        session.setAttribute("user", user);

        HttpCookie cookie = new HttpCookie(session.getId());

        return createLoginSuccessResponse(List.of(cookie.toString()));
    }
    private HttpResponse createRedirectResponse(String url) {
        return new HttpResponse(
                "302 FOUND ",
                Map.of("Location", List.of(url)),
                new byte[0]);
    }
    private HttpResponse createLoginSuccessResponse(List<String> httpCookie) {
        return new HttpResponse(
                "302 FOUND ",
                Map.of("Location", List.of("/index.html"),
                        "Set-Cookie", httpCookie),
                new byte[0]);
    }

    private HttpResponse createRegisterSuccessResponse(List<String> httpCookie) {
        return new HttpResponse(
                "302 FOUND ",
                Map.of("Location", List.of("/index.html"),
                        "Set-Cookie", httpCookie),
                new byte[0]);
    }

    private HttpResponse createUnauthorizedResponse() throws URISyntaxException, IOException {
        Path filePath = Path.of(getClass().getClassLoader().getResource("static/401.html").toURI());
        byte[] body = Files.readAllBytes(filePath);

        return new HttpResponse(
                "302 FOUND ",
                Map.of("Content-Type", List.of("text/html; charset=UTF-8")),
                body);
    }

    private HttpResponse createResourceResponse(String requestPath) throws URISyntaxException, IOException {
        Path filePath = resolveResourcePath(requestPath);
        byte[] body = Files.readAllBytes(filePath);

        return new HttpResponse(
                "200 OK ",
                Map.of(
                        "Content-Type",
                        List.of(resolveContentType(filePath.getFileName().toString()))
                ), body);
    }

    // 리소스 경로 찾기
    private Path resolveResourcePath(String requestPath) throws URISyntaxException {
        String resourcePath = "static" + requestPath;
        if (requestPath.equals("/login")) resourcePath += ".html";
        if (requestPath.equals("/register")) resourcePath += ".html";
        Path filePath = Path.of(
                getClass()
                        .getClassLoader()
                        .getResource(resourcePath)
                        .toURI()
        );

        log.info("requestPath: {}", requestPath);
        log.info("filePath: {}", filePath);

        return filePath;
    }

    // 헤더 분리
    private Map<String, List<String>> readRequestHeaders(InputStream inputStream) throws IOException {
        String headerLine;
        Map<String, List<String>> requestHeaders = new HashMap<>();

        while ((headerLine = readLine(inputStream)) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(':');

            if (colonIndex <= 0) {
                throw new IOException("Invalid header line: " + headerLine);
            }

            String headerName = headerLine.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
            String headerValue = headerLine.substring(colonIndex + 1).trim();

            requestHeaders.computeIfAbsent(headerName, key -> new ArrayList<>())
                    .add(headerValue);
        }

        return requestHeaders;
    }

    // requestBody 읽기
    private String readRequestBody(InputStream inputStream, Map<String, List<String>> requestHeaders) throws IOException {

        List<String> contentLengthValues = requestHeaders.getOrDefault("content-length", List.of("0"));

        if (contentLengthValues.isEmpty()) {
            return "";
        }

        String contentLengthValue = contentLengthValues.getFirst().trim();

        if (!contentLengthValue.matches("\\d+")) {
            throw new IOException(
                    "Invalid Content-Length: " + contentLengthValue
            );
        }

        for (String value : contentLengthValues) {
            if (!contentLengthValue.equals(value.trim())) {
                throw new IOException("Conflicting Content-Length headers");
            }
        }

        int contentLength = Integer.parseInt(contentLengthValue);

        if (contentLength < 0) {
            throw new IOException("Invalid Content-Length");
        }

        byte[] body = new byte[contentLength];
        inputStream.read(body, 0, contentLength);

        return new String(body);
    }

    // 쿼리 파라미터 분리
    private Map<String, String> parseQueryParameters(String requestTarget) {
        String[] targetParts = requestTarget.split("\\?", 2);
        Map<String, String> queryParameters = new HashMap<>();

        if (targetParts.length == 2) {
            String queryString = targetParts[1];

            // 본격 쿼리 파싱
            String[] parameters = queryString.split("&", -1);
            for (String parameter : parameters) {
                String[] parameterParts = parameter.split("=", 2);
                String parameterName = parameterParts[0];
                String parameterValue = parameterParts[1];
                queryParameters.put(parameterName, parameterValue);
            }
        }

        return queryParameters;
    }

    // content-type 결정
    private String resolveContentType(final String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html;charset=utf-8 ";
        }
        if (resourcePath.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (resourcePath.endsWith(".png")) {
            return "image/png";
        }
        if (resourcePath.endsWith(".jpg") || resourcePath.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "text/plain";
    }
}
