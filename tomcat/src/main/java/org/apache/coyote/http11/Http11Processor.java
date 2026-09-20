package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String BAD_REQUEST_MESSAGE = "잘못된 요청입니다.";

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String NOT_FOUND_PATH = "/404.html";

    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String JS_EXTENSION = ".js";
    private static final String JS_CONTENT_TYPE = "text/javascript";
    private static final int PATH_INDEX = 1;
    private static final String STATIC_PREFIX = "static";
    private static final int REQUEST_LINE_SIZE = 3;

    private static final String OK = "200 OK";
    private static final String BAD_REQUEST = "400 Bad Request";
    private static final String NOT_FOUND = "404 Not Found";

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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            readHeaders(reader);

            final var response = createResponse(requestLine);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String requestLine) throws IOException {
        if (requestLine.isBlank() || requestLine.trim().split(" ").length != REQUEST_LINE_SIZE) {
            return buildResponse(BAD_REQUEST, DEFAULT_CONTENT_TYPE, BAD_REQUEST_MESSAGE);
        }

        final String requestUri = parseUri(requestLine);
        final String requestPath = parsePath(requestUri);
        log.info("requestPath = " + requestPath);

        if (LOGIN_PATH.equals(requestPath)) {
            login(parseQueryParams(requestUri));
        }

        try {
            final String responseBody = resolveResponseBody(requestPath);
            return buildResponse(OK, resolveContentType(requestPath), responseBody);
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            return buildResponse(NOT_FOUND, DEFAULT_CONTENT_TYPE, resolveResponseBody(NOT_FOUND_PATH));
        }
    }

    private String buildResponse(final String status, final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String parseUri(String requestLine) {
        return requestLine.trim().split(" ")[PATH_INDEX];
    }

    private String parsePath(String requestUri) {
        final int index = requestUri.indexOf("?");
        if (index == -1) {
            return requestUri;
        }
        return requestUri.substring(0, index);
    }

    private Map<String, String> parseQueryParams(final String requestUri) {
        final int index = requestUri.indexOf("?");
        if (index == -1) {
            return Map.of();
        }
        final Map<String, String> queryParams = new HashMap<>();
        for (final String param : requestUri.substring(index + 1).split("&")) {
            final String[] keyAndValue = param.split("=", 2);
            if (keyAndValue.length == 2) {
                queryParams.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return queryParams;
    }

    private void login(final Map<String, String> queryParams) {
        final String account = queryParams.get("account");
        if (account == null) {
            return;
        }
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            log.info("존재하지 않는 계정입니다: {}", account);
            return;
        }
        final User foundUser = user.get();
        log.info("{}", foundUser);
        log.info("비밀번호 일치 여부: {}", foundUser.checkPassword(queryParams.get("password")));
    }

    private String resolveContentType(String requestPath) {
        if (requestPath.endsWith(CSS_EXTENSION)) {
            return CSS_CONTENT_TYPE;
        }
        if (requestPath.endsWith(JS_EXTENSION)) {
            return JS_CONTENT_TYPE;
        }
        return DEFAULT_CONTENT_TYPE;
    }

    private List<String> readHeaders(BufferedReader reader) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            headers.add(line);
        }
        return headers;
    }

    private String resolveResponseBody(String requestPath) throws IOException {
        if (ROOT_PATH.equals(requestPath)) {
            return DEFAULT_MESSAGE;
        }
        if (!requestPath.contains(".")) {
            requestPath += HTML_EXTENSION;
        }
        return readStaticFile(requestPath);
    }

    private String readStaticFile(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(STATIC_PREFIX + resourcePath);
        if (resource == null) {
            throw new RuntimeException("요청한 리소스를 찾을 수 없습니다: " + resourcePath);
        }

        try {
            final Path path = Path.of(resource.toURI());

            if (!Files.isRegularFile(path)) {
                throw new RuntimeException("요청한 리소스를 찾을 수 없습니다: " + resourcePath);
            }
            return Files.readString(path);
        } catch (URISyntaxException e) {
            throw new IOException("잘못된 리소스 경로입니다: " + resourcePath, e);
        }
    }
}
