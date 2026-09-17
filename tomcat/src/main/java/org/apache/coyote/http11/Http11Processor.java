package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.text.html.Option;
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

            final var reader = new BufferedReader(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8));

            final String requestLine = reader.readLine();
            if (requestLine == null) return;

            final String requestTarget = requestLine.split(" ")[1];
            final String[] targetParts = requestTarget.split("\\?", 2);
            final String path = targetParts[0];
            final String queryString = targetParts.length == 2 ? targetParts[1] : "";
            final Map<String, String> queryParameters = parseQueryString(queryString);

            login(path, queryParameters);
//            레벨2에 사용 예쩡
//            String line;
//            while ((line = reader.readLine()) != null && !line.isEmpty()) {
//
//            }

            final byte[] responseBody = createResponseBody(path);
            if (responseBody == null) {
                return;
            }

            final var responseHeader = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + resolveContentType(path) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");


            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        return "text/html";
    }

    private byte[] createResponseBody(final String path) throws IOException {
        if ("/".equals(path)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if ("/login".equals(path)) {
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

    private void login(final String path, final Map<String, String> queryParameters) {
        if (!"/login".equals(path)) {
            return;
        }

        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");
        if (account == null || password == null) {
            return;
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            if (user.get().checkPassword(password)) {
                log.info("로그인 사용자 조회 성공: {}", user);
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
