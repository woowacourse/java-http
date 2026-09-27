package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_PAGE = "/register.html";
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
            final HttpRequest request = new HttpRequest(reader);

            if (isLoginPostRequest(request)) {
                write(outputStream, loginResponse(request));
                return;
            }
            if (isLoginPageRequest(request) && isLoggedIn(request.getCookie())) {
                write(outputStream, redirectResponse(INDEX_PAGE));
                return;
            }
            if (isRegisterPostRequest(request)) {
                write(outputStream, redirectResponse(registerLocation(request)));
                return;
            }
            write(outputStream, staticResourceResponse(resourcePath(request.getPath())));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isLoginPostRequest(final HttpRequest request) {
        return request.isPost() && request.getPath().equals(LOGIN_PATH);
    }

    private String loginResponse(final HttpRequest request) {
        final User existUser = InMemoryUserRepository.findByAccount(request.getFormParameter("account"))
                .filter(user -> user.checkPassword(request.getFormParameter("password")))
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

    private boolean isLoginPageRequest(final HttpRequest request) {
        return request.isGet() && request.getPath().equals(LOGIN_PATH);
    }

    private boolean isLoggedIn(final HttpCookie cookie) {
        return cookie.getJSessionId()
                .map(SessionManager.getInstance()::findSession)
                .map(session -> session.getAttribute(SESSION_USER))
                .isPresent();
    }

    private boolean isRegisterPostRequest(final HttpRequest request) {
        return request.isPost() && request.getPath().equals(REGISTER_PATH);
    }

    private String registerLocation(final HttpRequest request) {
        User registerUser = new User(
                request.getFormParameter("account"),
                request.getFormParameter("password"),
                request.getFormParameter("email"));
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
        if (path.equals(ROOT_PATH)) {
            return INDEX_PAGE;
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
