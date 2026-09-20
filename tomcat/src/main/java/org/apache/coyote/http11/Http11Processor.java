package org.apache.coyote.http11;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.HttpResponse;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
        try (BufferedReader bufferedReader =
                     new BufferedReader(
                             new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {

            // 첫번째 라인 & 요청 url 구하기
            String requestLine = bufferedReader.readLine();
            String requestTarget = requestLine.split(" ")[1];

            // 리소스 경로 찾기
            String requestPath = requestTarget.split("\\?")[0];

            // 헤더 읽기
            Map<String, String> requestHeaders = readRequestHeaders(bufferedReader);

            // 쿼리 파싱
            Map<String, String> queryParameters = parseQueryParameters(requestTarget);

            HttpResponse httpResponse;
            if (requestPath.equals("/")) {
                httpResponse = new HttpResponse(
                        "200 OK ",
                        Map.of("Content-Type",  "text/html;charset=utf-8 "),
                        "Hello world!".getBytes()
                );
            } else if (requestPath.startsWith("/login"))
                httpResponse = handleLoginRequest(queryParameters);
            else {
                httpResponse = createResourceResponse(requestPath);
            }

        writeHttpResponse(outputStream, httpResponse);
    } catch (IOException | UncheckedServletException e) {
        log.error(e.getMessage(), e);
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    }
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

    private String buildHttpResponse(String statusCode, String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private HttpResponse handleLoginRequest(Map<String, String> queryParameters)
            throws URISyntaxException, IOException {

        String account = queryParameters.get("account");
        String password = queryParameters.get("password");

        if (account == null || password == null) {
            return createResourceResponse("/login.html");
        }

        if (!authenticateUser(account, password)) {
            return createUnauthorizedResponse();
        }

        return createLoginSuccessResponse();
    }

    private HttpResponse createLoginSuccessResponse() {
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
    private Map<String, String> readRequestHeaders(BufferedReader reader) throws IOException {
        String headerLine;
        Map<String, String> requestHeaders = new HashMap<>();

        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
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

        User user = InMemoryUserRepository
                .findByAccount(account)
                .orElseThrow();

        if (user.checkPassword(password)) {
            log.info("user : {}", user.toString());
            return true;
        }
        return false;
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
