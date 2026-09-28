package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private static final Map<String, String> RESOURCE_PATH_BY_REQUEST_PATH = Map.of(
            LOGIN_PATH,  "/login.html",
            REGISTER_PATH, "/register.html"
    );

    private static final Map<String, String> CONTENT_TYPE_BY_EXTENSION = Map.of(
            ".html", "text/html;charset=utf-8",
            ".css", "text/css;charset=utf-8",
            ".js", "application/javascript;charset=utf-8"
    );

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
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final String[] requestParts = requestLine.split(" ");
            final String method = requestParts[0];
            final String path = requestParts[1].split("\\?", 2)[0];
            final Map<String, String> headers = readHeaders(reader);
            final HttpCookie cookies = new HttpCookie(headers.get("Cookie"));
            final String sessionId = cookies.get(SESSION_COOKIE_NAME);
            Session session = SESSION_MANAGER.findSession(sessionId);
            String setCookieHeader = null;

            if (sessionId == null || sessionId.isBlank()) {
                session = SESSION_MANAGER.createSession();
                setCookieHeader = "Set=Cookie: " + SESSION_COOKIE_NAME + "=" + session.getId();
            }

            if ("GET".equals(method) && LOGIN_PATH.equals(path) && session != null && session.getAttribute("user") != null) {
                outputStream.write(redirect("/index.html", setCookieHeader).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            if ("POST".equals(method) && (REGISTER_PATH.equals(path) || LOGIN_PATH.equals(path))) {
                final int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
                final String requestBody = readBody(reader, contentLength);
                final Map<String, String> parameters = parseForm(requestBody);

                final String location;
                if (REGISTER_PATH.equals(path)) {
                    location = register(parameters) ? "/index.html" : REGISTER_PATH;
                } else {
                    final Optional<User> user = login(parameters);

                    if (user.isPresent()) {
                        if (session == null) {
                            session = SESSION_MANAGER.createSession();
                            setCookieHeader = "Set-Cookie: " + SESSION_COOKIE_NAME + "=" + session.getId();
                        }
                        session.setAttribute("user", user.get());
                        location = "/index.html";
                    } else {
                        location = "/401.html";
                    }
                }

                outputStream.write(redirect(location, setCookieHeader).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final ResponseData responseData = loadResponseData(path);
            outputStream.write(
                    response(responseData.body(), responseData.contentType(), setCookieHeader).getBytes(StandardCharsets.UTF_8)
            );

            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            final int colon = line.indexOf(':');
            if (colon > 0) {
                headers.put(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
            }
        }

        return headers;
    }

    private String readBody(final BufferedReader reader, final int length) throws IOException {
        final char[] buffer = new char[length];
        int offset = 0;

        while (offset < length) {
            final int count = reader.read(buffer, offset, length - offset);
            if (count == -1) {
                throw new IOException("요청 본문을 끝까지 읽지 못했습니다.");
            }
            offset += count;
        }
        return new String(buffer);
    }

    private Map<String, String> parseForm(final String body) {
        final Map<String, String> parameters = new HashMap<>();

        for (String pair : body.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            final String[] entry = pair.split("=", 2);
            final String name = URLDecoder.decode(entry[0], StandardCharsets.UTF_8);
            final String keyValue = entry.length == 2 ? URLDecoder.decode(entry[1], StandardCharsets.UTF_8) : "";
            parameters.put(name, keyValue);
        }
        return parameters;
    }

    private boolean register(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        final String email = parameters.get("email");

        if (account == null || account.isBlank() || password == null || password.isBlank() || email == null || email.isBlank()) {
            return false;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return true;
    }

    private Optional<User> login(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(found -> found.checkPassword(password));

        user.ifPresent(found -> log.info("로그인한 회원: {}", account));
        return user;
    }

    private ResponseData loadResponseData(final String requestPath) throws IOException {
        if (ROOT_PATH.equals(requestPath)) {
            return new ResponseData(
                    DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8),
                    DEFAULT_CONTENT_TYPE
            );
        }

        final String resourcePath = STATIC_RESOURCE_DIRECTORY + RESOURCE_PATH_BY_REQUEST_PATH.getOrDefault(requestPath, requestPath);

        final InputStream resource = getClass().
                getClassLoader().
                getResourceAsStream(resourcePath);

        if (resource == null) {
            return new ResponseData(
                    DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8),
                    DEFAULT_CONTENT_TYPE
            );
        }

        try (resource) {
            return new ResponseData(resource.readAllBytes(), contentTypeOf(resourcePath));
        }
    }

    private String contentTypeOf(final String resourcePath) {
        return CONTENT_TYPE_BY_EXTENSION.entrySet().stream()
                .filter(entry -> resourcePath.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT_CONTENT_TYPE);
    }

    private record ResponseData(byte[] body, String contentType) {
    }

    private String response(final byte[] responseBody, final String contentType, final String setCookieHeader) {
        final List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 200 OK");

        if (setCookieHeader != null) {
            lines.add(setCookieHeader);
        }

        lines.add("Content-Type: " + contentType + " ");
        lines.add("Content-Length: " + responseBody.length + " ");
        lines.add("");
        lines.add(new String(responseBody, StandardCharsets.UTF_8));

        return String.join("\r\n", lines);
    }

    private String redirect(final String location, final String setCookieHeader) {
        final List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 302 Found");

        if (setCookieHeader != null) {
            lines.add(setCookieHeader);
        }

        lines.add("Location: " + location);
        lines.add("Content-Length: 0");
        lines.add("");
        lines.add("");

        return String.join("\r\n", lines);
    }

    private String redirect(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                "");
    }
}
