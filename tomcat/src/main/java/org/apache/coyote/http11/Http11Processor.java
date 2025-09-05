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
import java.util.HashMap;
import java.util.List;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // 그냥 기본 경로로 오면 responseBody에 "Hello world!" 반환하기,
            // (기본 경로 시 반환 값을 어떻게 설정할까? -> ex. 아무 설정 없으면 hello world, 내가 hello.html을 보여주고 싶으면 그 것을 보여주기)
            // 경로가 있다면 resources에서 찾기 (찾는 알고리즘에 대해서 고민해보기)
            // 확장자에 따른 찾는 위치 고민하기

            // 1. startLine을 읽는다.
            final String startLine = bufferedReader.readLine();

            // 2. 경로를 추출한다.
            final String[] startLineElements = startLine.split(" ");
            final String httpPath = startLineElements[1];
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                // 헤더들을 읽기만 하고 무시
            }

            String response;
            if (httpPath.isEmpty() || httpPath.equals("/")) {
                String responseBody = "Hello world!";
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "text/html;charset=utf-8");
                response = responseBuilder("200", "OK", headers, responseBody);
            } else if (httpPath.startsWith("/login")) {
                // 로그인 요청 처리
                response = handleLoginRequest(httpPath);
            } else {
                // 3. 해당 경로에 파일이 있는지 검사한다
                String filePath = httpPath.startsWith("/") ? httpPath.substring(1) : httpPath;
                URL resource = getClass().getClassLoader().getResource("static/" + filePath);

                // 4. 있다면 body에 넣어서 준다. | 없다면 404 반환한다
                if (resource == null) {
                    URL notFoundResource = getClass().getClassLoader().getResource("static/404.html");
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "text/html;charset=utf-8");

                    if (notFoundResource == null || notFoundResource.getPath() == null) {
                        String responseBody = "<html><body><h1>404 Not Found</h1></body></html>";
                        response = responseBuilder("404", "Not Found", headers, responseBody);
                    } else {
                        Path path = new File(notFoundResource.getPath()).toPath();
                        List<String> contents = Files.readAllLines(path);
                        String responseBody = String.join("\n", contents);
                        response = responseBuilder("404", "Not Found", headers, responseBody);
                    }
                } else {
                    Path path = new File(resource.getPath()).toPath();
                    List<String> contents = Files.readAllLines(path);
                    String responseBody = String.join("\n", contents);
                    responseBody += "\n";
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", getContentType(filePath));

                    response = responseBuilder("200", "OK", headers, responseBody);
                }
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleLoginRequest(String httpPath) throws IOException {
        if (httpPath.contains("?")) {
            Map<String, String> queryParams = parseQueryString(httpPath);
            String account = queryParams.get("account");
            String password = queryParams.get("password");

            if (account != null && password != null) {
                User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
                if (user.checkPassword(password)) {
                    log.info("로그인 성공: {}", user);
                }
            }
        }

        URL loginResource = getClass().getClassLoader().getResource("static/login.html");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");

        if (loginResource == null) {
            String responseBody = "<html><body><h1>Login Page Not Found</h1></body></html>";
            return responseBuilder("404", "Not Found", headers, responseBody);
        } else {
            Path path = new File(loginResource.getPath()).toPath();
            List<String> contents = Files.readAllLines(path);
            String responseBody = String.join("\n", contents);
            return responseBuilder("200", "OK", headers, responseBody);
        }
    }

    private Map<String, String> parseQueryString(String httpPath) {
        Map<String, String> params = new HashMap<>();

        int queryStart = httpPath.indexOf("?");
        if (queryStart == -1) {
            return params;
        }

        String queryString = httpPath.substring(queryStart + 1);
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }

    private String responseBuilder(String httpStatusCode, String httpStatusMessage, Map<String, String> headers, String responseBody) {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1").append(" ").append(httpStatusCode).append(" ").append(httpStatusMessage).append("\r\n");
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
            }
        }
        response.append("Content-Length: ").append(responseBody.getBytes().length).append("\r\n");
        response.append("\r\n");
        response.append(responseBody);

        return response.toString();
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
