package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static/";

    private final Socket connection;
    private final Manager manager;

    public Http11Processor(final Socket connection, Manager manager) {
        this.connection = connection;
        this.manager = manager;
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
             var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final var requestLine = RequestLine.from(reader.readLine());
            final var requestHeaders = RequestHeaders.from(reader);
            final var requestCookies = RequestCookies.from(requestHeaders.getHeader("Cookie"));
            final Map<String, String> responseHeaders = new HashMap<>();

            var session = manager.findSession(requestCookies.getCookie("JSESSIONID"));
            if (session == null) {
                session = Session.create(manager);
                final var sessionCookie = new ResponseCookie("JSESSIONID", session.getId());
                responseHeaders.put("Set-Cookie", sessionCookie.toHeaderString());
            }

            //=========== POST 요청 처리 ============
            if (requestLine.getMethod() == HttpMethod.POST) {
                final var contentLength = requestHeaders.getHeader("Content-Length");
                final var requestBody = readRequestBody(reader, contentLength);
                final Map<String, String> parameters = RequestBodyUtils.parseFormUrlEncoded(requestBody);
                String redirectUrl = "/index.html";

                if ("login".equals(requestLine.getPath())) {
                    String account = parameters.get("account");
                    String password = parameters.get("password");
                    final Optional<User> optionalUser = findUserByAccount(account);
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        if (user.checkPassword(password)) {
                            session.setAttribute("user", user);
                            log.info("로그인 성공 account: {}", account);
                        }
                    } else {
                        redirectUrl = "/401.html";
                        log.info("로그인 실패 account: {}", account);
                    }
                }

                if ("register".equals(requestLine.getPath())) {
                    final var newUser = new User(
                            parameters.get("account"),
                            parameters.get("password"),
                            parameters.get("email")
                    );
                    InMemoryUserRepository.save(newUser);
                    log.info("Registered new user: {}", newUser.getAccount());
                }

                responseHeaders.put("Location", redirectUrl);
                final var response = buildHttpResponse("302 Found", "text/html", "", responseHeaders);
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            //=========== GET 요청 처리 ============
            var requestPath = requestLine.getPath();

            if ("/login".equals(requestPath) && session.getAttribute("user") != null) {
                responseHeaders.put("Location", "/index.html");
                final var response = buildHttpResponse("302 Found", "text/html", "", responseHeaders);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (requestPath.isBlank() || "/".equals(requestPath)) {
                requestPath = "index.html";
            }

            var statusCode = "200 OK";

            var responseBody = readStaticFileContent(requestPath);
            if (responseBody == null) {
                statusCode = "404 Not Found";
                responseBody = readStaticFileContent("404.html");

                if (responseBody == null) {
                    responseBody = "<h1>404 Not Found</h1>";
                }
            }

            final var contentType = ContentType.from(requestPath);
            final var response = buildHttpResponse(statusCode, contentType.getMimeType(), responseBody,
                    responseHeaders);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try (final var outputStream = connection.getOutputStream()) {
                String responseBody;
                URL errorResource = getResourceFrom("500.html");
                if (errorResource != null) {
                    responseBody = Files.readString(Paths.get(errorResource.toURI()));
                } else {
                    responseBody = "<h1>500 Internal Server Error</h1>";
                }

                final var response = buildHttpResponse("500 Internal Server Error", "text/html", responseBody,
                        Collections.emptyMap());
                outputStream.write(response.getBytes());
                outputStream.flush();
            } catch (IOException | URISyntaxException ex) {
                log.error("500 에러 전송 실패: " + ex.getMessage(), ex);
            }
        }
    }

    public Optional<User> findUserByAccount(String account) {
        if (account == null || account.isBlank()) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account);
    }

    private URL getResourceFrom(String requestPath) {
        var resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + requestPath);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + requestPath + ".html");
        }
        return resource;
    }

    private String readStaticFileContent(String requestPath) throws URISyntaxException, IOException {
        URL resource = getResourceFrom(requestPath);
        if (resource == null) {
            return null;
        }
        return Files.readString(Paths.get(resource.toURI()));
    }

    private String buildHttpResponse(String statusCode, String mimeType, String responseBody,
                                     Map<String, String> additionalResponseHeaders) throws IOException {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ").append(statusCode).append("\r\n");
        response.append("Content-Type: ").append(mimeType).append(";charset=utf-8\r\n");
        byte[] bodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        response.append("Content-Length: ").append(bodyBytes.length).append("\r\n");

        for (Map.Entry<String, String> header : additionalResponseHeaders.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        response.append("\r\n"); // 헤더와 본문 구분
        response.append(responseBody);
        return response.toString();
    }

    private String readRequestBody(BufferedReader reader, String contentLengthHeader) throws IOException {
        if (contentLengthHeader == null || contentLengthHeader.isBlank()) {
            return "";
        }
        final int contentLength = Integer.parseInt(contentLengthHeader);
        if (contentLength <= 0) {
            return "";
        }
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
