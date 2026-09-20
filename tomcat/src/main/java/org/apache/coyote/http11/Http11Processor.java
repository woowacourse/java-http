package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    public static final String FAVICON_PATH = "/favicon.ico";
    public static final String STATIC_PATH = "static";
    public static final String QUERY_DELIMITER = "?";
    public static final String PARAM_DELIMITER = "&";
    public static final String PARAM_EQUAL = "=";

    public static final String SLASH = "/";
    public static final String EXTENSION_DELIMITER = ".";
    public static final String HTML_EXTENSION = ".html";
    public static final String CSS_EXTENSION = ".css";
    public static final String JS_EXTENSION = ".js";

    public static final String CSS_CONTENT_TYPE = "text/css";
    public static final String JS_CONTENT_TYPE = "text/javascript";
    public static final String HTML_CONTENT_TYPE = "text/html";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        try {
            process(connection);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(final Socket connection) throws URISyntaxException {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String uri = getUri(bufferedReader);
            if (uri.equals(FAVICON_PATH)) {
                return;
            }

            int index = uri.indexOf(QUERY_DELIMITER);
            String path = findPath(uri, index);
            Map<String, String> queryParams = findQueryString(uri, index);

            HttpStatus httpStatus = HttpStatus.OK;

            if (uri.contains("login")) {
                httpStatus = findUser(queryParams);
            }

            if (httpStatus == HttpStatus.FOUND) {
                final String response = String.join("\r\n",
                        "HTTP/1.1 " + httpStatus.getHttpStatus() + " ",
                        "Location: /index.html ",
                        "Content-Length: 0 ",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.flush();

                return;
            }

            if (httpStatus == HttpStatus.UNAUTHORIZED) {
                final Path filePath = getPath("static/401.html");
                final String responseBody = findResponseBody(filePath);

                final String response = String.join("\r\n",
                        "HTTP/1.1 " + httpStatus.getHttpStatus() + " ",
                        "Content-Type: " + findContentType(filePath) + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();

                return;
            }

            final Path filePath = getPath(path);
            final String responseBody = findResponseBody(filePath);

            final String response = String.join("\r\n",
                    "HTTP/1.1 " + httpStatus.getHttpStatus() + " ",
                    "Content-Type: " + findContentType(filePath) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUri(BufferedReader bufferedReader) throws IOException {
        String[] header = bufferedReader.readLine().split(" ");
        return header[1];
    }

    private String findPath(String uri, int index) {
        String path = uri;
        if (index != -1) {
            path = uri.substring(0, index);
        }

        if (!path.isBlank()) {
            if (!path.equals(SLASH) && !path.contains(EXTENSION_DELIMITER)) {
                path += HTML_EXTENSION;
            }
        }

        return STATIC_PATH + path;
    }

    private Map<String, String> findQueryString(String uri, int index) {
        String queryString = "";
        if (index != -1) {
            queryString = uri.substring(index + 1);
        }

        Map<String, String> queryParams = new HashMap<>();

        if (queryString.isBlank()) {
            return queryParams;
        }

        for (String query : queryString.split(PARAM_DELIMITER)) {
            String[] q = query.split(PARAM_EQUAL, 2);
            if (q.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(q[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(q[1], StandardCharsets.UTF_8);

            queryParams.put(key, value);
        }

        return queryParams;
    }

    private Path getPath(String path) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(path);
        return Paths.get(Objects.requireNonNull(resource).toURI());
    }

    private String findContentType(Path filePath) {
        if (filePath.toString().endsWith(CSS_EXTENSION)) {
            return CSS_CONTENT_TYPE;
        } else if (filePath.toString().endsWith(JS_EXTENSION)) {
            return JS_CONTENT_TYPE;
        }
        return HTML_CONTENT_TYPE;
    }

    private String findResponseBody(Path filePath) throws IOException {
        if (Files.isDirectory(filePath)) {
            return "Hello world!";
        }

        return Files.readString(filePath);
    }

    private HttpStatus findUser(Map<String, String> queryParams) {
        if (queryParams.isEmpty()) {
            return HttpStatus.OK;
        }

        final String account = queryParams.get("account");
        if (account == null || account.isBlank()) {
            log.info("아이디는 필수값입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        final String password = queryParams.get("password");
        if (password == null || password.isBlank()) {
            log.info("비밀번호는 필수값입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return HttpStatus.UNAUTHORIZED;
        }

        User foundUser = user.get();
        if (checkPassword(queryParams, foundUser)) {
            log.info("user: {}", foundUser);
            return HttpStatus.FOUND;
        }

        return HttpStatus.UNAUTHORIZED;
    }

    private boolean checkPassword(Map<String, String> queryParams, User user) {
        if (!user.checkPassword(queryParams.get("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return false;
        }

        return true;
    }

    public enum HttpStatus {

        OK(200, "OK"),
        FOUND(302, "Found"),
        UNAUTHORIZED(401, "Unauthorized");

        int value;
        String message;

        HttpStatus(int value, String message) {
            this.value = value;
            this.message = message;
        }

        public String getHttpStatus() {
            return this.value + " " + this.message;
        }
    }
}
