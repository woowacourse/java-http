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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String OK = "HTTP/1.1 200 OK ";
    private static final String NOT_FOUND = "HTTP/1.1 404 Not Found ";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_BODY = "Hello world!";
    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String LOGIN_PATH = "/login";
    private static final String ROOT_PATH = "/";
    private static final String FOUND = "HTTP/1.1 302 Found ";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String POST = "POST";
    private static final String REGISTER_PATH = "/register";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String requestLine = bufferedReader.readLine();
            final Map<String, String> headers = readHeaders(bufferedReader);
            final String body = readBody(bufferedReader, headers);
            final String response = createResponse(requestLine, headers, body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String requestLine, final Map<String, String> headers,
                                  final String body) throws IOException {
        final String setCookie = createSetCookie(headers);
        if (requestLine == null) {
            return buildResponse(OK, DEFAULT_CONTENT_TYPE, DEFAULT_BODY, setCookie);
        }

        final String method = requestLine.split(" ")[0];
        final String uri = requestLine.split(" ")[1];
        final String path = parsePath(uri);

        if (method.equals(POST) && path.equals(REGISTER_PATH)) {
            return createRegisterResponse(body, setCookie);
        }
        if (method.equals(POST) && path.equals(LOGIN_PATH)) {
            return createLoginResponse(body, setCookie);
        }

        return createResourceResponse(path, setCookie);
    }

    private String createSetCookie(final Map<String, String> headers) {
        final Cookie cookie = Cookie.from(headers.get(COOKIE_HEADER));
        if (cookie.hasJSessionId()) {
            return "";
        }
        return JSESSIONID + "=" + UUID.randomUUID();
    }

    private String parsePath(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private String buildRedirectResponse(final String location, final String setCookie) {
        final List<String> lines = new ArrayList<>();
        lines.add(FOUND);
        lines.add("Location: " + location + " ");
        addSetCookie(lines, setCookie);
        lines.add("");
        lines.add("");
        return String.join("\r\n", lines);
    }

    private void addSetCookie(final List<String> lines, final String setCookie) {
        if (!setCookie.isEmpty()) {
            lines.add("Set-Cookie: " + setCookie + " ");
        }
    }

    private String createLoginResponse(final String body, final String setCookie) {
        if (login(body)) {
            return buildRedirectResponse(INDEX_PAGE, setCookie);
        }
        return buildRedirectResponse(UNAUTHORIZED_PAGE, setCookie);
    }

    private String createRegisterResponse(final String body, final String setCookie) {
        register(body);
        return buildRedirectResponse(INDEX_PAGE, setCookie);
    }

    private boolean login(final String queryString) {
        final Map<String, String> params = parseParams(queryString);
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"))
                .filter(it -> it.checkPassword(params.get("password")));
        user.ifPresent(it -> log.info("{}", it));
        return user.isPresent();
    }

    private void register(final String body) {
        final Map<String, String> params = parseParams(body);
        final User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email"));
        InMemoryUserRepository.save(user);
        log.info("회원가입: {}", user);
    }

    private String createResourceResponse(final String path, final String setCookie) throws IOException {
        if (path.equals(ROOT_PATH)) {
            return buildResponse(OK, DEFAULT_CONTENT_TYPE, DEFAULT_BODY, setCookie);
        }

        final String resourcePath = toResourcePath(path);
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            final URL notFound = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
            return buildResponse(NOT_FOUND, DEFAULT_CONTENT_TYPE, readResource(notFound), setCookie);
        }

        return buildResponse(OK, getContentType(resourcePath), readResource(resource), setCookie);
    }

    private String buildResponse(final String statusLine, final String contentType,
                                 final String body, final String setCookie) {
        final List<String> lines = new ArrayList<>();
        lines.add(statusLine);
        addSetCookie(lines, setCookie);
        lines.add("Content-Type: " + contentType + ";charset=utf-8 ");
        lines.add("Content-Length: " + body.getBytes().length + " ");
        lines.add("");
        lines.add(body);
        return String.join("\r\n", lines);
    }

    private String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript";
        }
        return DEFAULT_CONTENT_TYPE;
    }

    private String readResource(final URL resource) throws IOException {
        return Files.readString(Path.of(resource.getFile()));
    }

    private String toResourcePath(final String path) {
        if (path.contains(".")) {
            return "static" + path;
        }
        return "static" + path + ".html";
    }

    private Map<String, String> parseParams(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        for (final String parameter : queryString.split("&")) {
            final String[] keyAndValue = parameter.split("=", 2);
            if (keyAndValue.length == 2) {
                params.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return params;
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] keyAndValue = line.split(": ", 2);
            if (keyAndValue.length == 2) {
                headers.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return headers;
    }

    private String readBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        final String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return "";
        }
        final char[] buffer = new char[Integer.parseInt(contentLength)];
        reader.read(buffer);
        return new String(buffer);
    }
}
