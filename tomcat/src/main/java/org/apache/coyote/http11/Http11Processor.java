package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            String[] tokens = requestLine.split(" ");
            String uri = tokens[1];

            String path = uri;
            String queryString = null;

            if (uri.contains("?")) {
                int queryIndex = uri.indexOf("?");
                path = uri.substring(0, queryIndex);
                queryString = uri.substring(queryIndex + 1);
            }

            if ("/".equals(path)) {
                handleRoot(outputStream);
                return;
            }

            if ("/login".equals(path)) {
                handleLogin(outputStream, queryString);
                return;
            }

            handleStaticResource(outputStream, path);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    private void handleRoot(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";
        sendOkResponse(outputStream, "text/html;charset=utf-8", responseBody.getBytes(StandardCharsets.UTF_8));
    }


    private void handleLogin(OutputStream outputStream, String queryString) throws IOException {
        if (queryString != null) {
            Map<String, String> params = parseQueryString(queryString);
            String account = params.get("account");
            String password = params.get("password");

            log.info("로그인 시도: account={}, password={}", account, password);
            Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

            if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
                log.info("로그인 성공: {}", userOptional.get());
            } else {
                log.info("로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        }

        loadStaticResource(outputStream, "/login.html");
    }

    private void handleStaticResource(OutputStream outputStream, String path) throws IOException {
        loadStaticResource(outputStream, path);
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private void loadStaticResource(OutputStream outputStream, String path) throws IOException {
        String resourcePath = "static" + path;
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (fileInputStream == null) {
                sendNotFoundResponse(outputStream);
                return;
            }

            String contentType = determineContentType(path);
            byte[] bodyBytes = fileInputStream.readAllBytes();
            sendOkResponse(outputStream, contentType, bodyBytes);
        }
    }

    private String determineContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private void sendOkResponse(OutputStream outputStream, String contentType, byte[] body) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + body.length + " ",
                "",
                "");

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private void sendNotFoundResponse(OutputStream outputStream) throws IOException {
        final var responseBody = "404 Not Found";
        final var bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        final var response = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: " + bodyBytes.length,
                "",
                responseBody);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
