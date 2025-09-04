package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var inputStream = connection.getInputStream();
                var outputStream = connection.getOutputStream()) {

            String requestLine = readRequestLine(inputStream);
            if (requestLine == null || requestLine.isEmpty()) {
                sendResponse(400, "Bad Request", "text/plain;charset=utf-8", new byte[0], outputStream);
                return;
            }

            String[] request = requestLine.split(" ");
            String fullPath = request[1];

            String requestPath = extractPath(fullPath);
            Map<String, String> queryParams = extractQueryParams(fullPath);

            if ("/".equals(requestPath)) {
                byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
                sendResponse(200, "OK", "text/html;charset=utf-8", body, outputStream);
            } else if ("/login".equals(requestPath)) {
                handleLogin(queryParams, outputStream);
            } else {
                handleStaticResource(fullPath, requestPath, outputStream);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestLine(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.readLine();
    }

    private String extractPath(String fullPath) {
        int index = fullPath.indexOf("?");

        if (index != -1) {
            return fullPath.substring(0, index);
        }
        return fullPath;
    }

    private Map<String, String> extractQueryParams(String fullPath) {
        Map<String, String> queryParams = new HashMap<>();
        int index = fullPath.indexOf("?");
        if (index == -1) {
            return queryParams;
        }

        String queryString = fullPath.substring(index + 1);
        for (String pairs : queryString.split("&")) {
            String[] pair = pairs.split("=", 2);
            if (pair.length == 2) {
                queryParams.put(pair[0], pair[1]);
            }
        }
        return queryParams;
    }

    private void handleLogin(Map<String, String> queryParams, OutputStream outputStream) throws IOException {
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        InMemoryUserRepository.findByAccount(account).ifPresentOrElse(user -> {
            if (user.checkPassword(password)) {
                log.info("user: {}", user);
            } else {
                log.info("login fail(password mismatch) account: {}", account);
            }
        }, () -> log.info("login fail(not found account) account: {}", account));

        sendResponse(200, "OK", "text/plain;charset=utf-8", "".getBytes(), outputStream);
    }

    private void handleStaticResource(String fullPath, String requestPath, OutputStream outputStream)
            throws IOException {
        URL resourceUrl = ClassLoader.getSystemResource("static" + requestPath);
        if (resourceUrl == null) {
            sendResponse(404, "Not Found", "text/html;charset=utf-8", new byte[0], outputStream);
            return;
        }

        byte[] fileBytes = Files.readAllBytes(Paths.get(resourceUrl.getPath()));
        String contentType = determineContentType(fullPath);

        sendResponse(200, "OK", contentType, fileBytes, outputStream);
    }

    private String determineContentType(String fullPath) {
        if (fullPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (fullPath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private void sendResponse(int statusCode,
                              String statusMessage,
                              String contentType,
                              byte[] body,
                              OutputStream outputStream) throws IOException {
        String responseHeader = String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode + " " + statusMessage,
                "Content-Type: " + contentType,
                "Content-Length: " + body.length,
                "",
                ""
        );
        outputStream.write(responseHeader.getBytes());
        outputStream.write(body);
        outputStream.flush();
    }
}
