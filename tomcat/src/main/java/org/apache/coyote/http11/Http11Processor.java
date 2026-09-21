package org.apache.coyote.http11;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.HttpCookie;
import com.techcourse.model.HttpResponse;
import com.techcourse.model.User;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            Map<String, String> requestHeaders = readRequestHeaders(inputStream);

            // 바디 읽기
            String requestBody = readRequestBody(inputStream, requestHeaders);
            Map<String, String> formParameters = parseFormParameters(requestBody);

            // 쿼리 파싱
            Map<String, String> queryParameters = parseQueryParameters(requestTarget);

            HttpResponse httpResponse;
            if (requestPath.equals("/")) {
                httpResponse = new HttpResponse(
                        "200 OK ",
                        Map.of("Content-Type", "text/html;charset=utf-8 "),
                        "Hello world!".getBytes()
                );
            } else if (requestPath.startsWith("/login")) {
                httpResponse = handleLoginRequest(formParameters, requestHeaders);
            } else if (requestPath.startsWith("/register") && requestMethod.equals("POST")) {
                User user = new User(formParameters.get("account"), formParameters.get("password"), formParameters.get("email"));
                InMemoryUserRepository.save(user);
                httpResponse = createRegisterSuccessResponse();
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
            partsMap.put(keyValue[0], keyValue[1]);
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

        httpResponse.headers().forEach((name, value) ->
                header.append(name).append(": ")
                        .append(value).append("\r\n"));

        header.append("Content-Length: ")
                .append(body.length)
                .append(" ")
                .append("\r\n\r\n");

        outputStream.write(header.toString().getBytes());
        outputStream.write(body);

    }

    private HttpResponse handleLoginRequest(Map<String, String> formParameters, Map<String, String> requestHeaders)
            throws URISyntaxException, IOException {

        String account = formParameters.get("account");
        String password = formParameters.get("password");

        if (account == null || password == null) {
            return createResourceResponse("/login.html");
        }

        if (!authenticateUser(account, password)) {
            return createUnauthorizedResponse();
        }

        String cookieLine = requestHeaders.get("Cookie");
        String httpCookie = findHttpCookie(cookieLine);

        return createLoginSuccessResponse(httpCookie);
    }

    private String findHttpCookie(String cookieLine) {
        if (cookieLine == null) { return HttpCookie.makeJsessionid().toString(); }

        Map<String, String> cookieParts = new HashMap<>();

        String[] cookies = cookieLine.split(";");

        for (String cookie : cookies) {
            String[] parts = cookie.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            cookieParts.put(parts[0].trim(), parts[1].trim());
        }
        if (cookieParts.isEmpty() || cookieParts.get("JSESSIONID") == null) {
            return HttpCookie.makeJsessionid().toString();
        }
        return cookieParts.get("JSESSIONID");
    }

    private HttpResponse createLoginSuccessResponse(String httpCookie) {
        return new HttpResponse(
                "302 FOUND ",
                Map.of("Location", "/index.html",
                        "Set-Cookie", httpCookie),
                new byte[0]);
    }

    private HttpResponse createRegisterSuccessResponse() {
        return new HttpResponse(
                "302 FOUND ",
                Map.of("Location", "/index.html"),
                new byte[0]);
    }

    private HttpResponse createUnauthorizedResponse() throws URISyntaxException, IOException {
        Path filePath = Path.of(getClass().getClassLoader().getResource("static/401.html").toURI());
        byte[] body = Files.readAllBytes(filePath);

        return new HttpResponse(
                "401 Unauthorized ",
                Map.of("Content-Type", "text/html; charset=UTF-8"),
                body);
    }

    private HttpResponse createResourceResponse(String requestPath) throws URISyntaxException, IOException {
        Path filePath = resolveResourcePath(requestPath);
        byte[] body = Files.readAllBytes(filePath);

        return new HttpResponse(
                "200 OK ",
                Map.of(
                        "Content-Type",
                        resolveContentType(filePath.getFileName().toString())
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
    private Map<String, String> readRequestHeaders(InputStream inputStream) throws IOException {
        String headerLine;
        Map<String, String> requestHeaders = new HashMap<>();

        while ((headerLine = readLine(inputStream)) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(":", 2);

            if (headerParts.length != 2) {
                continue;
            }

            String headerName = headerParts[0].trim();
            String headerValue = headerParts[1].trim();

            requestHeaders.put(headerName, headerValue);
        }

        return requestHeaders;
    }

    // requestBody 읽기
    private String readRequestBody(InputStream inputStream, Map<String, String> requestHeaders) throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.getOrDefault("Content-Length", "0"));
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
            String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                String[] parameterParts = parameter.split("=", 2);
                String parameterName = parameterParts[0];
                String parameterValue = parameterParts[1];
                queryParameters.put(parameterName, parameterValue);
            }
        }

        return queryParameters;
    }

    // 로그인
    private boolean authenticateUser(String account, String password) {

        return InMemoryUserRepository
                .findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
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
