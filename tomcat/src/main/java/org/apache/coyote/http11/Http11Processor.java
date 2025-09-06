package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            // 1. 요청 라인 파싱
            final String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            String[] parsedLine = requestLine.split(" ");
            String requestUri = parsedLine[1];

            String path = requestUri;
            Map<String, String> queryParams = Collections.emptyMap();

            // 2. 쿼리 파라미터 분리
            if (requestUri.contains("?")) {
                int index = requestUri.indexOf("?");
                path = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);
                queryParams = parseQueryString(queryString);
            }

            // 3. 로그인 처리
            if ("/login".equals(path)) {
                String account = queryParams.get("account");
                String password = queryParams.get("password");

                if (account != null && password != null) {
                    InMemoryUserRepository.findByAccount(account)
                            .ifPresentOrElse(user -> {
                                if (user.checkPassword(password)) {
                                    log.info("user : {}", user);
                                } else {
                                    log.info("Login failed for account: {}", account);
                                }
                            }, () -> log.info("No such account: {}", account));
                }
            }

            // 4. 리소스 로딩
            URL resource;
            if (requestUri.endsWith(".html") || requestUri.endsWith(".css") || requestUri.endsWith(".js")) {
                resource = getClass().getClassLoader().getResource("static" + requestUri);
            } else if ("/login".equals(path)) {
                resource = getClass().getClassLoader().getResource("static" + requestUri + ".html");
            } else {
                resource = getClass().getClassLoader().getResource(requestUri);
            }

            // 5. 응답 바디 및 상태 결정
            final String responseBody;
            final String statusLine;

            if (resource != null) {
                responseBody = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
                statusLine = "HTTP/1.1 200 OK ";
            } else if ("/".equals(path)) {
                responseBody = "Hello world!";
                statusLine = "HTTP/1.1 200 OK ";
            } else {
                responseBody = "";
                statusLine = "HTTP/1.1 404 Not Found";
            }

            // 6. Content-Type 결정
            String contentType = "text/plain;charset=utf-8";
            if (path.endsWith(".html") || "/login".equals(path) || "/".equals(path)) {
                contentType = "text/html;charset=utf-8";
            } else if (path.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8";
            }

            // 7. 응답 전송
            final var response = String.join("\r\n",
                    statusLine,
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody
            );

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
