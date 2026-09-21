package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            final String[] parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }

            final String method = parts[0];
            final String requestTarget = parts[1];
            final String[] targetParts = requestTarget.split("\\?", 2);
            final String path = targetParts[0];
            final String httpVersion = parts[2];

            log.info("method: {}, path: {}, version: {}",
                    method, path, httpVersion);

            if ("/login".equals(path) && targetParts.length == 2) {
                final Map<String, String> parameters = parseQuery(targetParts[1]);

                final String account = parameters.get("account");
                final String password = parameters.get("password");

                if (account != null && password != null) {
                    final boolean authenticated = InMemoryUserRepository.findByAccount(account)
                            .filter(user -> user.checkPassword(password))
                            .isPresent();

                    if (authenticated) {
                        log.info("회원 조회 성공: {}", account);

                        final String response = String.join("\r\n",
                                "HTTP/1.1 302 Found",
                                "Location: /index.html",
                                "Content-Length: 0",
                                "",
                                "");

                        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        return;
                    }
                }
            }

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            String contentType = "text/html;charset=utf-8";

            if ("/index.html".equals(path)
                    || "/css/styles.css".equals(path)
                    || path.endsWith(".js")
                    || "/login".equals(path)) {

                if ("/css/styles.css".equals(path)) {
                    contentType = "text/css;charset=utf-8";
                } else if (path.endsWith(".js")) {
                    contentType = "text/javascript;charset=utf-8";
                }

                final String resourcePath = "/login".equals(path)
                        ? "static/login.html"
                        : "static" + path;

                responseBody = readResource(resourcePath);
            }
            final String responseHeader = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 쿼리 인코딩입니다.");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQuery(String query) {
        final Map<String, String> parameters = new HashMap<>();

        for (String parameter : query.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length != 2) {
                continue;
            }

            final String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            parameters.put(key, value);
        }

        return parameters;
    }

    private byte[] readResource(String resourcePath) throws IOException {
        try (var resource = getClass().getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (resource == null) {
                throw new IOException(resourcePath + " 파일을 찾을 수 없습니다.");
            }

            return resource.readAllBytes();
        }
    }
}
