package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final RequestLine requestLine = new RequestLine(bufferedReader.readLine());
            final Headers headers = readHeaders(bufferedReader);

            String path = requestLine.getPath();
            String code = "200";
            String status = "OK";

            if (requestLine.isPost()) {
                final int contentLength = headers.contentLength();
                final char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);

                final String requestBody = new String(buffer);
                final Map<String, String> parameters = parseQueryString(requestBody);

                if ("/register".equals(path)) {
                    register(parameters);
                    path = "/index";
                    code = "200";
                    status = "OK";
                } else if ("/login".equals(path)) {
                    if (login(parameters)) {
                        path = "/index";
                        code = "302";
                        status = "FOUND";
                    } else {
                        path = "/401";
                        code = "401";
                        status = "UNAUTHORIZED";
                    }
                }
                path += ".html";
            }
            if (requestLine.isGet()) {
                path = resolveGetPath(path);
            }

            final var response = makeResponse(path, code, status);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String resolveGetPath(final String requestPath) {
        return switch (requestPath) {
            case "/" -> "/index.html";
            case "/login" -> "/login.html";
            case "/register" -> "/register.html";
            default -> requestPath;
        };
    }

    private String makeResponse(
            final String path,
            final String code,
            final String status
    ) throws IOException {
        final String responseBody = getResponseBody(path);
        final String contentType = resolveContentType(path);

        return String.join("\r\n",
                "HTTP/1.1 " + code + " " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String getResponseBody(final String path) throws IOException {
        final String resourceName = "static" + path;
        final String fileName = Objects.requireNonNull(
                getClass().getClassLoader().getResource(resourceName),
                "리소스를 찾을 수 없음: " + resourceName
        ).getPath();

        return Files.readString(Path.of(fileName));
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();
        if (queryString.isBlank()) {
            return parameters;
        }

        for (String pair : queryString.split("&")) {
            final String[] nameAndValue = pair.split("=", 2);
            if (nameAndValue.length != 2) {
                continue;
            }
            parameters.put(nameAndValue[0].trim(), nameAndValue[1].trim());
        }
        return parameters;
    }

    private boolean login(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        final Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (loginUser.isEmpty()) {
            return false;
        }

        log.info("로그인 성공: {}", loginUser);
        return true;
    }

    private void register(final Map<String, String> parameters) {
        final User user = new User(
                parameters.get("account"),
                parameters.get("password"),
                parameters.get("email")
        );

        InMemoryUserRepository.save(user);
        log.info("회원가입 성공: {}", user);
    }

    private static Headers readHeaders(final BufferedReader bufferedReader) throws IOException {
        final Headers headers = new Headers();

        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                throw new IllegalArgumentException();
            }
            headers.add(line);
            line = bufferedReader.readLine();
        }
        return headers;
    }
}
