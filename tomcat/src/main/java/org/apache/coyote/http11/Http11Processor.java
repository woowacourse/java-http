package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info(
                "connect host: {}, port: {}",
                connection.getInetAddress(),
                connection.getPort()
        );

        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            String method = parseMethod(requestLine);
            String uri = parseUri(requestLine);
            String queryString = extractQueryString(uri);
            String resourcePath = normalizePath(parsePath(uri));

            Map<String, String> headers = readHeaders(reader);

            if (isPostLogin(method, resourcePath)) {
                String requestBody = readRequestBody(reader, headers);
                writeLoginResponse(outputStream, requestBody);
                return;
            }

            if (isPostRegister(method, resourcePath)) {
                String requestBody = readRequestBody(reader, headers);
                writeRegisterResponse(outputStream, requestBody);
                return;
            }

            logUserIfExists(resourcePath, queryString);

            URL resource = findResource(resourcePath);

            if (resource == null) {
                writeNotFoundResponse(outputStream);
                return;
            }

            byte[] body = readBody(resource);

            String response = createResponse("200 OK", resourcePath, body);
            writeResponse(outputStream, response, body);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseMethod(String requestLine) {
        String[] parts = requestLine.split(" ");
        return parts[0];
    }

    private String parseUri(String requestLine) {
        String[] parts = requestLine.split(" ");

        // 추후 Request Line 형식 검증 추가 예정
        return parts[1];
    }

    private String parsePath(String uri) {
        int queryIndex = uri.indexOf("?");
        if (queryIndex == -1) {
            return uri;
        }

        return uri.substring(0, queryIndex);
    }

    private String extractQueryString(String uri) {
        int queryIndex = uri.indexOf("?");
        if (queryIndex == -1) {
            return null;
        }

        return uri.substring(queryIndex + 1);
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header;

        while ((header = reader.readLine()) != null && !header.isEmpty()) {
            String[] nameAndValue = header.split(":", 2);
            if (nameAndValue.length == 2) {
                headers.put(nameAndValue[0].trim(), nameAndValue[1].trim());
            }
        }

        return headers;
    }

    private String readRequestBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String contentLengthHeader = headers.get("Content-Length");

        if (contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private boolean isPostLogin(String method, String resourcePath) {
        return method.equals("POST") && resourcePath.equals("/login.html");
    }

    private boolean isPostRegister(String method, String resourcePath) {
        return method.equals("POST") && resourcePath.equals("/register.html");
    }

    private void writeLoginResponse(OutputStream outputStream, String requestBody) throws IOException {
        Map<String, String> params = parseQueryString(requestBody);
        String account = params.get("account");
        String password = params.get("password");

        if (account == null || password == null) {
            writeRedirectResponse(outputStream, "/401.html");
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> {
                            log.info("조회된 사용자: id={}, account={}", user.getId(), user.getAccount());
                            writeRedirectResponse(outputStream, "/index.html");
                        },
                        () -> writeRedirectResponse(outputStream, "/401.html")
                );
    }

    private void writeRegisterResponse(OutputStream outputStream, String requestBody) {
        Map<String, String> params = parseQueryString(requestBody);
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        InMemoryUserRepository.save(new User(account, password, email));
        writeRedirectResponse(outputStream, "/index.html");
    }

    private void logUserIfExists(String resourcePath, String queryString) {
        if (!isLoginPath(resourcePath) || queryString == null) {
            return;
        }

        Map<String, String> queryParams = parseQueryString(queryString);
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("조회된 사용자: id={}, account={}", user.getId(), user.getAccount())
                );
    }

    private boolean isLoginPath(String resourcePath) {
        return resourcePath.equals("/login.html");
    }

    private String normalizePath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }

        if (!path.contains(".")) {
            return path + ".html";
        }

        return path;
    }

    private URL findResource(String resourcePath) {
        return getClass()
                .getClassLoader()
                .getResource("static" + resourcePath);
    }

    private byte[] readBody(URL resource) throws IOException {
        try (InputStream resourceStream = resource.openStream()) {
            return resourceStream.readAllBytes();
        }
    }

    private void writeRedirectResponse(OutputStream outputStream, String location) {
        String response = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                ""
        );

        try {
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private void writeNotFoundResponse(OutputStream outputStream) throws IOException {
        String resourcePath = "/404.html";
        URL resource = findResource(resourcePath);

        if (resource == null) {
            return;
        }

        byte[] body = readBody(resource);
        String response = createResponse("404 Not Found", resourcePath, body);
        writeResponse(outputStream, response, body);
    }

    private String createResponse(String status, String resourcePath, byte[] body) {
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + getContentType(resourcePath),
                "Content-Length: " + body.length,
                "",
                ""
        );
    }

    private void writeResponse(
            OutputStream outputStream,
            String response,
            byte[] body
    ) throws IOException {

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0];
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            queryParams.put(key, value);
        }

        return queryParams;
    }

}
