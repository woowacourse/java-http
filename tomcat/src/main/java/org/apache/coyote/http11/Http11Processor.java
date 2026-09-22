package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final long DEFAULT_USER_ID = 999L;

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = buildResponse(httpRequest);
            addSessionCookie(httpRequest, httpResponse);
            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionCookie(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getCookies().hasSessionId()) {
            return;
        }

        httpResponse.setHeader("Set-Cookie", "JSESSIONID=" + httpRequest.getOrCreateSession().getId());
    }

    private HttpResponse buildResponse(HttpRequest httpRequest) {
        if ("/login".equals(httpRequest.getPath())) {
            return buildLoginResponse(httpRequest);
        }
        if ("/register".equals(httpRequest.getPath())) {
            return buildRegisterResponse(httpRequest);
        }
        if ("/".equals(httpRequest.getPath())) {
            return buildRootResponse();
        }

        return buildResourceResponse(httpRequest.getPath());
    }

    private HttpResponse buildLoginResponse(HttpRequest httpRequest) {
        Session session = httpRequest.findSession();
        if ("GET".equals(httpRequest.getMethod()) && isLoggedIn(session)) {
            return buildRedirectResponse("/index.html");
        }

        if (!"POST".equals(httpRequest.getMethod())) {
            return buildResourceResponse("/login.html");
        }

        String account = httpRequest.getBody().get("account");
        String password = httpRequest.getBody().get("password");
        var user = InMemoryUserRepository.findByAccountAndPassword(account, password);
        if (user.isPresent()) {
            httpRequest.getOrCreateSession().setAttribute("user", user.get());
            return buildRedirectResponse("/index.html");
        }

        return buildRedirectResponse("/401.html");
    }

    private boolean isLoggedIn(Session session) {
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse buildRegisterResponse(HttpRequest httpRequest) {
        if (!"POST".equals(httpRequest.getMethod())) {
            return buildResourceResponse("/register.html");
        }

        register(httpRequest.getBody());

        return buildRedirectResponse("/index.html");
    }

    private void register(Map<String, String> requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        User user = new User(DEFAULT_USER_ID, account, password, email);
        InMemoryUserRepository.save(user);
    }

    private HttpResponse buildRedirectResponse(String location) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.redirect(location);
        return httpResponse;
    }

    private HttpResponse buildRootResponse() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setBody("Hello world!", "text/html;charset=utf-8");
        return httpResponse;
    }

    private HttpResponse buildResourceResponse(String path) {
        final var resource = findResource(path);
        if (resource == null) {
            throw new RuntimeException("자원을 찾을 수 없습니다.");
        }

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setBody(resource, getContentType(path));
        return httpResponse;
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "application/octet-stream";
    }

    private String findResource(String path) {
        try (final var inputStream = Http11Processor.class
                .getClassLoader()
                .getResourceAsStream("static" + path)) {
            if (inputStream == null) {
                return null;
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
