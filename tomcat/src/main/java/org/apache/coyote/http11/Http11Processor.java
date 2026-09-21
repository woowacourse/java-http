package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            final String requestLine = bufferedReader.readLine();
            final String[] requestLineParts = requestLine.split(" "); // 요청 첫 줄(Request Line) 분리
            final String requestUri = requestLineParts[1];
            final String requestMethod = requestLineParts[0];

            String line;
            final Map<String, String> httpRequestHeaders = new HashMap<>();

            while ((line = bufferedReader.readLine()) != null) { // 바디 전까지
                if (line.isEmpty()) {
                    break; // 빈 줄이면 헤더 끝
                }

                final String[] keyValue = line.split(":", 2);

                if (keyValue.length == 2) {
                    httpRequestHeaders.put(keyValue[0], keyValue[1].trim());
                }
            }

            final String path = extractPath(requestUri);
            final String queryString = extractQueryString(requestUri);

            final String rawParams;
            if ("POST".equals(requestMethod)) {
                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                rawParams = new String(buffer);
            } else {
                rawParams = queryString;
            }

            final Map<String, String> requestParams = parseFormData(rawParams);
            final String response;

            if ("POST".equals(requestMethod) && "/register".equals(path)) {
                register(requestParams);
                response = buildRedirectResponse("/index.html");
            } else if ("POST".equals(requestMethod) && "/login".equals(path)) {
                if (isLoginSuccess(requestParams)) {
                    response = buildRedirectResponse("/index.html");
                } else {
                    response = buildRedirectResponse("/401.html");
                }
            } else if ("/".equals(path)) {
                response = buildOkResponse("text/html", "Hello world!");
            } else {
                response = buildOkResponse(resolveContentType(path), readStaticFile(path));
            }

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractPath(final String requestUri) {
        final int queryStartIndex = requestUri.indexOf('?');
        if (queryStartIndex == -1) { // 쿼리가 없는 경우
            return requestUri;
        }
        return requestUri.substring(0, queryStartIndex);
    }

    private String extractQueryString(String requestUri) {
        final int queryStartIndex = requestUri.indexOf('?');
        if (queryStartIndex == -1) { // 쿼리가 없는 경우
            return "";
        }
        return requestUri.substring(queryStartIndex + 1);
    }

    private Map<String, String> parseFormData(final String queryString) {
        final Map<String, String> requestParams = new HashMap<>();
        if (queryString.isEmpty()) {
            return requestParams;
        }

        for (final String pair : queryString.split("&")) { // "account=gugu", "password=password"
            final String[] keyValue = pair.split("=", 2); // ["account", "gugu"]
            if (keyValue.length == 2) {
                requestParams.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)); // "account" -> "gugu"
            }
        }
        return requestParams;
    }

    private void register(final Map<String, String> requestParams) {
        final String account = requestParams.get("account");
        final String password = requestParams.get("password");
        final String email = requestParams.get("email");
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private boolean isLoginSuccess(final Map<String, String> queryParams) {
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");

        if (account == null) {
            return false;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(u -> u.checkPassword(password));
        user.ifPresent(u -> log.info("로그인 성공! user: {}", u));

        return user.isPresent();
    }

    private String readStaticFile(final String path) throws URISyntaxException, IOException {
        final String fileName = "static" + resolveFileName(path);
        final URL url = ClassLoader.getSystemResource(fileName); // 클래스패스에서 static/ 아래 파일을 찾아 실제 위치를 URL로 돌려 줌
        final File file = new File(url.toURI());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    // 요청 path를 받아서, 서버에서 찾을 파일 이름을 돌려 줌
    private String resolveFileName(final String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private String buildOkResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private String buildRedirectResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "",
                "");
    }
}
