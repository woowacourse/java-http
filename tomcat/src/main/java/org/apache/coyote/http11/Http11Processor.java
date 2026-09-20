package org.apache.coyote.http11;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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

            var responseBody = "Hello world!";
            String contentType = "text/html;charset=utf-8 ";

            // 첫번째 라인 & 요청 url 구하기
            String requestLine = bufferedReader.readLine();
            String requestTarget = requestLine.split(" ")[1];

            if (!requestTarget.equals("/")) {

                // 리소스 경로 찾기
                String requestPath = requestTarget.split("\\?")[0];
                Path filePath = resolveResourcePath(requestPath);

                // 헤더 읽기
                Map<String, String> requestHeaders = readRequestHeaders(bufferedReader);

                // 쿼리 파싱
                Map<String, String> queryParameters = parseQueryParameters(requestTarget);

                // 응답 바디 생성
                responseBody = Files.readString(filePath);

                // 로그인 처리
                authenticateUser(requestPath,  queryParameters);

                // content-type 처리
                contentType = resolveContentType(filePath.getFileName().toString());

            }

            final var response = buildHttpResponse(responseBody, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
    private void authenticateUser(String requestPath, Map<String, String> queryParameters) {
        if (requestPath.startsWith("/login")
                && queryParameters.containsKey("account")
                && queryParameters.containsKey("password")
        ) {
            String account = queryParameters.get("account");
            String password = queryParameters.get("password");

            User user = InMemoryUserRepository
                    .findByAccount(account)
                    .orElseThrow();

            if (user.checkPassword(password)) {
                log.info("login ok");
            }
        }
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

    private String buildHttpResponse(String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
