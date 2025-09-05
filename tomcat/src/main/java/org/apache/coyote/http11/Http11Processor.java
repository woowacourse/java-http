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

            final String startLine = readRequestLine(bufferedReader);
            final String httpPath = extractHttpPath(startLine);
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                // 지금 요구사항에서는 헤더들을 읽기만 하고 무시
            }

            String response = buildResponse(httpPath);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestLine(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    private String extractHttpPath(String startLine) {
        final String[] startLineElements = startLine.split(" ");
        return startLineElements[1];
    }

    private String buildResponse(String httpPath) throws IOException {
        if (isRootPath(httpPath)) {
            return buildRootResponse();
        } else if (isLoginPath(httpPath)) {
            return handleLoginRequest(httpPath);
        } else {
            return handleStaticFileRequest(httpPath);
        }
    }

    private boolean isRootPath(String httpPath) {
        return httpPath.isEmpty() || httpPath.equals("/");
    }

    private boolean isLoginPath(String httpPath) {
        return httpPath.startsWith("/login");
    }

    private String buildRootResponse() {
        String responseBody = "Hello world!";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        return responseBuilder("200", "OK", headers, responseBody);
    }

    private String handleStaticFileRequest(String httpPath) throws IOException {
        String filePath = httpPath.startsWith("/") ? httpPath.substring(1) : httpPath;
        URL resource = getClass().getClassLoader().getResource("static/" + filePath);

        if (resource == null) {
            return buildNotFoundResponse();
        } else {
            return buildStaticFileResponse(resource, filePath);
        }
    }

    private String buildNotFoundResponse() throws IOException {
        URL notFoundResource = getClass().getClassLoader().getResource("static/404.html");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");

        if (notFoundResource == null || notFoundResource.getPath() == null) {
            String responseBody = "<html><body><h1>404 Not Found</h1></body></html>";
            return responseBuilder("404", "Not Found", headers, responseBody);
        } else {
            Path path = new File(notFoundResource.getPath()).toPath();
            List<String> contents = Files.readAllLines(path);
            String responseBody = String.join("\n", contents);
            return responseBuilder("404", "Not Found", headers, responseBody);
        }
    }

    private String buildStaticFileResponse(URL resource, String filePath) throws IOException {
        Path path = new File(resource.getPath()).toPath();
        List<String> contents = Files.readAllLines(path);
        String responseBody = String.join("\n", contents);
        responseBody += "\n";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", getContentType(filePath));

        return responseBuilder("200", "OK", headers, responseBody);
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
