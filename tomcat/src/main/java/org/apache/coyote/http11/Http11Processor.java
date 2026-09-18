package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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
        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            String[] requestLine = reader.readLine().split(" ");
            String uri = requestLine[1];

            String response = buildResponse(uri);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildResponse(String uri) {
        String path = extractPath(uri);

        if ("/login".equals(path)) {
            return buildLoginResponse(extractQueryParams(uri));
        }
        if ("/".equals(path)) {
            return buildRootResponse();
        }

        return buildResourceResponse(path);
    }

    private String extractPath(String uri) {
        if (!uri.contains("?")) {
            return uri;
        }

        int indexOfQueryDelimiter = uri.indexOf("?");
        return uri.substring(0, indexOfQueryDelimiter);
    }

    private Map<String, String> extractQueryParams(String uri) {
        if (!uri.contains("?")) {
            return Map.of();
        }

        int indexOfQueryDelimiter = uri.indexOf("?");
        String rawParams = uri.substring(indexOfQueryDelimiter + 1);
        if (rawParams.isEmpty()) {
            return Map.of();
        }

        Map<String, String> params = new LinkedHashMap<>();
        for (String rawParam : rawParams.split("&")) {
            if (rawParam.isEmpty()) {
                continue;
            }

            String[] nameAndValue = rawParam.split("=", 2);
            if (nameAndValue.length < 2) {
                params.put(nameAndValue[0], "");
            } else {
                params.put(nameAndValue[0], nameAndValue[1]);
            }
        }
        return params;
    }

    private String buildLoginResponse(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        if (shouldShowLoginPage(account, password)) {
            return buildResourceResponse("/login.html");
        }

        if (isLoginSuccessful(account, password)) {
            return String.join("\r\n",
                    "HTTP/1.1 302 Found",
                    "Location: /index.html",
                    "",
                    "");
        }

        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /401.html",
                "",
                "");
    }
    
    private boolean shouldShowLoginPage(
            String account,
            String password
    ) {
        return account == null && password == null;
    }

    private boolean isLoginSuccessful(
            String account,
            String password
    ) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password)
                .isPresent();
    }

    private String buildRootResponse() {
        String responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private String buildResourceResponse(String path) {
        final var resource = findResource(path);
        if (resource == null) {
            throw new RuntimeException("자원을 찾을 수 없습니다.");
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + getContentType(path) + " ",
                "Content-Length: " + resource.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                resource);
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "application/octet-stream";
    }

    private String findResource(String path) {
        try (final var inputStream = Http11Processor.class
                .getClassLoader()
                .getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                return null;
            }
            
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
