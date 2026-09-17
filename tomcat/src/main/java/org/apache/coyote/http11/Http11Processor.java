package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            String uri = requestLine.split(" ")[1];
            String path = extractPath(uri);
            Map<String, String> params = extractQueryParams(uri);

            if (path.equals("/login") && !params.isEmpty()) {
                login(params);
            }

            String statusLine = "200 OK";
            byte[] responseBody = createResponseBody(path);
            if (responseBody == null) {
                statusLine = "404 Not Found";
                responseBody = createResponseBody("/404.html");
            }
            String contentType = determineContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + statusLine + " ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractPath(final String uri) {
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    private Map<String, String> extractQueryParams(final String uri) {
        final Map<String, String> params = new HashMap<>();
        if (!uri.contains("?")) {
            return params;
        }
        final String queryString = uri.substring(uri.indexOf("?") + 1);
        for (String param : queryString.split("&")) {
            String[] kv = param.split("=");
            params.put(kv[0], kv[1]);
        }
        return params;
    }

    private void login(final Map<String, String> params) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        user.ifPresentOrElse(
                u -> {
                    if (u.checkPassword(params.get("password"))) {
                        log.info("로그인 성공! 아이디 : {}", u.getAccount());
                    } else {
                        log.info("비밀번호가 일치하지 않습니다.");
                    }
                },
                () -> log.info("존재하지 않는 계정입니다.")
        );
    }

    private byte[] createResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!".getBytes();
        }
        final String resourcePath = path.equals("/login") ? "/login.html" : path;
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            return null;
        }
        final File file = new File(resource.getFile());
        return Files.readAllBytes(file.toPath());
    }

    private String determineContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
