package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }
            final String[] lines = requestLine.split(" ");
            final String uri = lines[1];

            int queryIndex = uri.indexOf("?");

            String path = uri;
            String queryString = "";

            if (queryIndex != -1) {
                path = uri.substring(0, queryIndex);
                queryString = uri.substring(queryIndex + 1);
            }

            readHeaders(bufferedReader);

            if (path.equals("/login") && !queryString.isEmpty()) {
                boolean loginSuccess = login(queryString);

                String redirectPath = loginSuccess ? "/index.html" : "/401.html";

                String response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: " + redirectPath,
                        "Content-Length: 0",
                        "",
                        "");

                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            var responseBody = "Hello world!";
            int contentLength = responseBody.getBytes().length;
            String contentType = getContentType(path);
            String statusLine = "HTTP/1.1 200 OK ";

            if (!path.equals("/")) {
                String resourcePath = getResourcePath(path);

                byte[] fileBytes = readResource(resourcePath);

                if (fileBytes == null) {
                    fileBytes = readResource("static/404.html");
                    if (fileBytes == null) {
                        throw new IllegalArgumentException("404.html 리소스를 찾을 수 없습니다.");
                    }
                    statusLine = "HTTP/1.1 404 Not Found ";
                }

                responseBody = new String(fileBytes, StandardCharsets.UTF_8);
                contentLength = fileBytes.length;
            }

            final var response = String.join("\r\n",
                    statusLine,
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + contentLength + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException e) {
            log.error("HTTP 요청 처리 중 입출력 오류가 발생했습니다.", e);
        } catch (RuntimeException e) {
            log.error("HTTP 요청 처리 중 예상하지 못한 오류가 발생했습니다.", e);
        }
    }

    private byte[] readResource(String resourcePath) throws IOException, URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return null;
        }
        URI resourceUri = resourceUrl.toURI();
        Path path = Paths.get(resourceUri);

        return Files.readAllBytes(path);
    }

    private void readHeaders(BufferedReader bufferedReader) throws IOException {
        String headerLine = bufferedReader.readLine();
        while (headerLine != null && !headerLine.isEmpty()) {
            headerLine = bufferedReader.readLine();
            if (headerLine == null) {
                return;
            }
        }
    }

    private String getResourcePath(String path) {
        if (path.equals("/login")) {
            return "static/login.html";
        }
        return "static" + path;
    }

    private boolean login(String queryString) {
        Map<String, String> loginInfo = parseQueryString(queryString);

        String account = loginInfo.get("account");
        String password = loginInfo.get("password");

        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        var optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isEmpty()) {
            return false;
        }

        var user = optionalUser.get();

        if (!user.checkPassword(password)) {
            return false;
        }

        log.info("login user: {}", user);
        return true;
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            }
        }

        return parameters;
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
