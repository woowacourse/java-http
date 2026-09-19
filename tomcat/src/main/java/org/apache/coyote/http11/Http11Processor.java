package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
             final var outputStream = connection.getOutputStream()) {
            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final String requestTarget = requestLine.split(" ")[1];
            final String path = extractPath(requestTarget);
            final String queryString = extractQueryString(requestTarget);

            if (path.equals("/")) {
                final var responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            String filePath = "static" + path;
            String contentType = "text/html;charset=utf-8";

            if (path.equals("/login")) {
                filePath = "static/login.html";

                if (!queryString.isBlank()) {
                    login(parseQueryString(queryString));
                }
            }

            if (path.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            }

            if (path.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8";
            }


            final URL resource = getClass().getClassLoader().getResource(filePath);
            if (resource == null) {
                final var response404 = "HTTP/1.1 404 Not Found\r\n\r\n";
                outputStream.write(response404.getBytes());
                outputStream.flush();
                return;
            }

            final byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

            final var responseHeader = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + body.length + " ",
                    "",
                    "");

            outputStream.write(responseHeader.getBytes());
            outputStream.write(body);
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void login(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");

        if (account == null || password == null) {
            log.info("아이디 또는 비밀번호가 입력되지 않았습니다.");
            return;
        }

        log.info("로그인 시도 - account: {}, password: {}", account, password);

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));
        user.ifPresentOrElse(
                foundUser -> log.info("회원 조회 결과: {}", foundUser),
                () -> log.info("아이디 또는 비밀번호가 일치하지 않습니다. account: {}", account)
        );
    }

    private String extractPath(final String requestTarget) {
        final int queryIndex = requestTarget.indexOf("?");
        if (queryIndex == -1) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryIndex);
    }

    private String extractQueryString(final String requestTarget) {
        final int queryIndex = requestTarget.indexOf("?");
        if (queryIndex == -1) {
            return "";
        }
        return requestTarget.substring(queryIndex + 1);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return params;
        }

        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue[0].isBlank()) {
                continue;
            }
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                params.put(keyValue[0], "");
            }
        }
        return params;
    }
}
