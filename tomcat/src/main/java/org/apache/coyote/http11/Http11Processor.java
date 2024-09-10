package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResourceLoader;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            String response = generateResponse(inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(InputStream inputStream) throws IOException {
        HttpRequest request = HttpRequest.of(inputStream);
        if (request.isGet() && request.isSameUri("/")) {
            return renderDefaultPage(request);
        }
        if (request.isGet() && ContentMimeType.isEndsWithExtension(request.getPath())) {
            return renderStaticResource(request);
        }
        if (request.isSameUri("/login")) {
            return login(request);
        }
        if (request.isSameUri("/register")) {
            return register(request);
        }
        return renderHtmlPage(request);
    }

    private String renderDefaultPage(HttpRequest request) {
        HttpResponse response = new HttpResponse(HttpStatus.OK, request.getProtocolVersion(), new ResponseBody("text/html", "Hello world!"));
        return response.getResponse();
    }

    private String renderStaticResource(HttpRequest request) throws IOException {
        ResponseBody responseBody = ResourceLoader.loadStaticResource(request.getPath());
        HttpResponse response = new HttpResponse(HttpStatus.OK, request.getProtocolVersion(), responseBody);
        return response.getResponse();
    }

    private String renderHtmlPage(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse(HttpStatus.OK, request.getProtocolVersion(), ResourceLoader.loadHtmlResource(request.getPath()));
        return response.getResponse();
    }

    private String login(final HttpRequest request) throws IOException {
        if (request.isGet()) {
            final var cookie = new HttpCookie(request.getCookie());
            return createResponseBasedOnCookie(request, cookie);
        }
        if (request.isPost()) {
            return postLogin(request);
        }
        return renderHtmlPage(request);
    }

    private String createResponseBasedOnCookie(HttpRequest request, HttpCookie cookie) throws IOException {
        if (!cookie.hasJSESSIONID()) {
            return renderHtmlPage(request);
        }
        return createResponseBasedOnSession(request, cookie);
    }

    private String createResponseBasedOnSession(HttpRequest request, HttpCookie cookie) throws IOException {
        if (SessionManager.getInstance().hasSession(cookie.getJSESSIONID())) {
            return handleSessionExist(request, cookie);
        }
        return renderHtmlPage(request);
    }

    private String handleSessionExist(HttpRequest request, HttpCookie cookie) throws IOException {
        final var session = SessionManager.getInstance().findSession(cookie.getJSESSIONID());
        final var user = (User) session.getAttribute("user");
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty()) {
            return renderHtmlPage(request);
        }
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, request.getProtocolVersion());
        response.setRedirect("/index.html");
        return response.getResponse();
    }

    private String postLogin(HttpRequest request) {
        if (isAuthenticateUser(request)) {
            HttpCookie cookie = new HttpCookie(request.getCookie());
            return getLoginResponse(request, cookie);
        }
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, request.getProtocolVersion());
        response.setRedirect("/401.html");
        return response.getResponse();
    }

    private String getLoginResponse(HttpRequest request, HttpCookie cookie) {
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, request.getProtocolVersion());
        response.setRedirect("/index.html");
        if (!cookie.hasJSESSIONID() || !SessionManager.getInstance().hasSession(cookie.getJSESSIONID())) {
            final var session = getSession(request);
            response.setCookie("JSESSIONID", session.getId());
        }
        return response.getResponse();
    }

    private Session getSession(HttpRequest request) {
        final var session = new Session(UUID.randomUUID().toString());
        InMemoryUserRepository.findByAccount(request.findBodyValueByKey("account"))
                .ifPresent(user -> session.setAttribute("user", user));
        return session;
    }

    private String register(final HttpRequest request) throws IOException {
        if (request.isPost()) {
            final var newUser =
                    new User(request.findBodyValueByKey("account"), request.findBodyValueByKey("password"), request.findBodyValueByKey("email"));
            InMemoryUserRepository.save(newUser);
            HttpResponse response = new HttpResponse(HttpStatus.FOUND, request.getProtocolVersion());
            response.setRedirect("/index.html");
            return response.getResponse();
        }
        return renderHtmlPage(request);
    }

    private boolean isAuthenticateUser(final HttpRequest request) {
        return InMemoryUserRepository.findByAccount(request.findBodyValueByKey("account"))
                .filter(user -> user.checkPassword(request.findBodyValueByKey("password")))
                .map(user -> {
                    log.info("user : {}", user);
                    return true;
                })
                .orElseGet(() -> false);
    }
}
