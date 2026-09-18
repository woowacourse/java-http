package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
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

            final BufferedReader reader = createReader(inputStream);
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String requestTarget = extractRequestTarget(requestLine);
            final String path = extractPath(requestTarget);
            final Map<String, String> queryParameters = parseQueryParameters(requestTarget);

//            레벨2에 사용 예쩡
//            String line;
//            while ((line = reader.readLine()) != null && !line.isEmpty()) {
//
//            }
            final byte[] responseBody = handleRequest(path, queryParameters);
            writeResponse(outputStream, path, responseBody);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private BufferedReader createReader(final InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private String extractRequestTarget(final String requestLine) {
        final String[] requestLineParts = requestLine.trim().split("\\s+", 3);

        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException(
                    "올바르지 않은 HTTP 요청 라인입니다: " + requestLine
            );
        }

        return requestLineParts[1];
    }

    private String extractPath(final String requestTarget) {
        return requestTarget.split("\\?", 2)[0];
    }

    private Map<String, String> parseQueryParameters(final String requestTarget) {
        final String[] targetParts = requestTarget.split("\\?", 2);
        final String queryString = targetParts.length == 2 ? targetParts[1] : "";

        return parseQueryString(queryString);
    }

    private void writeResponse(final OutputStream outputStream, final String path,
                               final byte[] responseBody) throws IOException {
        if (responseBody == null) {
            return;
        }

        outputStream.write(createResponseHeader(path, responseBody.length));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private byte[] createResponseHeader(final String path, final int contentLength) {
        final String responseHeader = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + resolveContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "",
                "");

        return responseHeader.getBytes(StandardCharsets.UTF_8);
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        return "text/html";
    }

    private byte[] handleRequest(final String path,
                                 final Map<String, String> queryParameters) throws IOException {
        if ("/".equals(path)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if ("/login".equals(path)) {
            login(queryParameters);
            return readResource("/login.html");
        }

        return readResource(path);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> queryParameters = new HashMap<>();
        if (queryString.isBlank()) {
            return queryParameters;
        }

        for (final String parameter : queryString.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                final String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                queryParameters.put(name, value);
            }
        }
        return queryParameters;
    }

    private void login(final Map<String, String> queryParameters) {
        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");
        if (account == null || password == null) {
            return;
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            User foundUser = user.get();
            if (foundUser.checkPassword(password)) {
                log.info("로그인 사용자 조회 성공: {}", foundUser);
            }
            else {
                log.info("아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        }
    }

    private byte[] readResource(final String uri) throws IOException {
        try (InputStream resource = getClass().getClassLoader()
                .getResourceAsStream("static" + uri)) {
            if (resource == null) {
                return null;
            }

            return resource.readAllBytes();
        }
    }
}
