package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String STATIC_RESOURCE_DIRECTORY = "static/";
    private static final String DEFAULT_PAGE = "index.html";
    private static final String LOGIN_PATH = "login";
    private static final String LOGIN_PAGE = "login.html";
    private static final String REGISTER_PATH = "register";
    private static final String REGISTER_PAGE = "register.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String SESSION_USER = "user";

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final String[] requestLine = readRequestLine(reader);
            final String method = requestLine[0];
            final String uri = requestLine[1].substring(1);
            final String path = extractPath(uri);
            final Map<String, String> headers = readHeaders(reader);
            final String body = readBody(reader, headers);
            final Map<String, String> queryParams = parseQueryString(extractQueryString(uri));
            final Map<String, String> formData = parseQueryString(body);
            final HttpCookie cookie = new HttpCookie(headers.get("Cookie"));

            if (isLoginRequest(path, formData)) {
                write(outputStream, loginResponse(formData));
                return;
            }
            if (isLoginPageRequest(method, path) && isLoggedIn(cookie)) {
                write(outputStream, redirectResponse(INDEX_PAGE));
                return;
            }
            if (isRegisterRequest(path, formData)) {
                write(outputStream, redirectResponse(registerLocation(formData)));
                return;
            }
            write(outputStream, staticResourceResponse(resourcePath(path)));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            final int index = line.indexOf(":");
            if (index != -1) {
                headers.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
            }
            line = reader.readLine();
        }
        return headers;
    }

    private String[] readRequestLine(final BufferedReader reader) throws IOException {
        return reader.readLine().split(" ");
    }

    private String readBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return "";
        }
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, buffer.length);
        return new String(buffer);
    }

    private String extractPath(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private String extractQueryString(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return "";
        }
        return uri.substring(index + 1);
    }

    private Map<String, String> parseQueryString(String queryString) {
        final Map<String, String> params = new HashMap<>();
        queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8);

        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            final String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private boolean isLoginRequest(final String path, final Map<String, String> params) {
        return path.equals(LOGIN_PATH) && params.containsKey("account");
    }

    private String loginResponse(final Map<String, String> params) {
        final User existUser = InMemoryUserRepository.findByAccount(params.get("account"))
                .filter(user -> user.checkPassword(params.get("password")))
                .orElse(null);
        if (existUser == null) {
            return redirectResponse(UNAUTHORIZED_PAGE);
        }
        log.info("user : {}", existUser);
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(SESSION_USER, existUser);
        SessionManager.getInstance().add(session);
        return redirectResponse(INDEX_PAGE, HttpCookie.ofJSessionId(session.getId()));
    }

    private boolean isLoginPageRequest(final String method, final String path) {
        return method.equals("GET") && path.equals(LOGIN_PATH);
    }

    private boolean isLoggedIn(final HttpCookie cookie) {
        return cookie.getJSessionId()
                .map(SessionManager.getInstance()::findSession)
                .map(session -> session.getAttribute(SESSION_USER))
                .isPresent();
    }

    private boolean isRegisterRequest(String path, Map<String, String> params) {
        return path.equals(REGISTER_PATH) && params.containsKey("account") && params.containsKey("password");
    }

    private String registerLocation(final Map<String, String> params) {
        User registerUser = new User(params.get("account"), params.get("password"), params.get("email"));
        InMemoryUserRepository.save(registerUser);
        return INDEX_PAGE;
    }

    private String redirectResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "",
                "");
    }

    private String redirectResponse(final String location, final String cookie) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "Set-Cookie: " + cookie + " ",
                "",
                "");
    }

    private String resourcePath(final String path) {
        if (path.isEmpty()) {
            return DEFAULT_PAGE;
        }
        if (path.equals(LOGIN_PATH)) {
            return LOGIN_PAGE;
        }
        if (path.equals(REGISTER_PATH)) {
            return REGISTER_PAGE;
        }
        return path;
    }

    private String staticResourceResponse(final String path) throws IOException, URISyntaxException {
        final byte[] responseBody = readStaticResource(path);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentTypeOf(path) + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                new String(responseBody));
    }

    private byte[] readStaticResource(final String path) throws IOException, URISyntaxException {
        final URL url = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + path);
        return Files.readAllBytes(Path.of(url.toURI()));
    }

    private String contentTypeOf(final String path) {
        final String extension = path.substring(path.lastIndexOf(".") + 1);
        if (extension.equals("css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private void write(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
