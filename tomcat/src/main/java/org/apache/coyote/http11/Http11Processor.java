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
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.HttpCookies;
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

            sendResponse(inputStream, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendResponse(InputStream inputStream, OutputStream outputStream) throws IOException {
        HttpRequest request = HttpRequest.of(inputStream);
        if (request.isGet() && request.isSameUri("/")) {
            renderDefaultPage(outputStream, request);
            return;
        }
        if (request.isGet() && ContentMimeType.isEndsWithExtension(request.getPath())) {
            renderStaticResource(outputStream, request);
            return;
        }
        if (request.isSameUri("/login")) {
            login(outputStream, request);
            return;
        }
        if (request.isSameUri("/register")) {
            register(outputStream, request);
            return;
        }
        renderHtmlPage(outputStream, request);
    }

    private void renderDefaultPage(OutputStream outputStream, HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse(outputStream, HttpStatus.OK, request.getProtocolVersion(), new ResponseBody("text/html", "Hello world!"));
        response.sendResponse();
    }

    private void renderStaticResource(OutputStream outputStream, HttpRequest request) throws IOException {
        ResponseBody responseBody = ResourceLoader.loadStaticResource(request.getPath());
        HttpResponse response = new HttpResponse(outputStream, HttpStatus.OK, request.getProtocolVersion(), responseBody);
        response.sendResponse();
    }

    private void renderHtmlPage(OutputStream outputStream, HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse(outputStream, HttpStatus.OK, request.getProtocolVersion(), ResourceLoader.loadHtmlResource(request.getPath()));
        response.sendResponse();
    }

    private void login(OutputStream outputStream, HttpRequest request) throws IOException {
        if (request.isGet()) {
            HttpCookies cookie = new HttpCookies(request.getCookie());
            createResponseBasedOnCookie(outputStream, request, cookie);
            return;
        }
        if (request.isPost()) {
            postLogin(outputStream, request);
            return;
        }
        renderHtmlPage(outputStream, request);
    }

    private void createResponseBasedOnCookie(OutputStream outputStream, HttpRequest request, HttpCookies cookie) throws IOException {
        if (!cookie.hasJSESSIONID()) {
            renderHtmlPage(outputStream, request);
            return;
        }
        createResponseBasedOnSession(outputStream, request, cookie);
    }

    private void createResponseBasedOnSession(OutputStream outputStream, HttpRequest request, HttpCookies cookie) throws IOException {
        if (SessionManager.getInstance().hasSession(cookie.getJSESSIONID())) {
            handleSessionExist(outputStream, request, cookie);
        }
        renderHtmlPage(outputStream, request);
    }

    private void handleSessionExist(OutputStream outputStream, HttpRequest request, HttpCookies cookie) throws IOException {
        Session session = SessionManager.getInstance().findSession(cookie.getJSESSIONID());
        User user = (User) session.getAttribute("user");
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty()) {
            renderHtmlPage(outputStream, request);
            return;
        }
        HttpResponse response = new HttpResponse(outputStream, HttpStatus.FOUND, request.getProtocolVersion());
        response.sendRedirect("/index.html");
    }

    private void postLogin(OutputStream outputStream, HttpRequest request) throws IOException {
        if (isAuthenticateUser(request)) {
            HttpCookies cookie = new HttpCookies(request.getCookie());
            getLoginResponse(outputStream, request, cookie);
            return;
        }
        HttpResponse response = new HttpResponse(outputStream, HttpStatus.FOUND, request.getProtocolVersion());
        response.sendRedirect("/401.html");
    }

    private void getLoginResponse(OutputStream outputStream, HttpRequest request, HttpCookies cookie) throws IOException {
        HttpResponse response = new HttpResponse(outputStream, HttpStatus.FOUND, request.getProtocolVersion());
        if (!cookie.hasJSESSIONID() || !SessionManager.getInstance().hasSession(cookie.getJSESSIONID())) {
            Session session = getSession(request);
            response.setCookie(Cookie.createSessionCookie(session.getId()));
        }
        response.sendRedirect("/index.html");
    }

    private Session getSession(HttpRequest request) {
        final var session = new Session(UUID.randomUUID().toString());
        InMemoryUserRepository.findByAccount(request.findBodyValueByKey("account"))
                .ifPresent(user -> session.setAttribute("user", user));
        return session;
    }

    private void register(OutputStream outputStream, HttpRequest request) throws IOException {
        if (request.isPost()) {
            User newUser =
                    new User(request.findBodyValueByKey("account"), request.findBodyValueByKey("password"), request.findBodyValueByKey("email"));
            InMemoryUserRepository.save(newUser);
            HttpResponse response = new HttpResponse(outputStream, HttpStatus.FOUND, request.getProtocolVersion());
            response.sendRedirect("/index.html");
        }
        renderHtmlPage(outputStream, request);
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
