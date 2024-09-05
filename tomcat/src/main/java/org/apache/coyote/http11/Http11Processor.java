package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final Http11ResourceFinder resourceFinder;

    private static final Manager SESSION_MANAGER = new SessionManager();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceFinder = new Http11ResourceFinder();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (connection) {
            sendResponse(connection);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendResponse(Socket connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = connection.getOutputStream();
        Http11Request request = Http11Request.from(inputStream);

        String requestURI = request.requestUri();

        Path path = resourceFinder.find(requestURI);

        if (path.getFileName().toString().equals("login.html")) {
            login(request, outputStream);
        }
        if (path.getFileName().toString().equals("register.html")) {
            register(request, outputStream);
        }

        sendStaticResourceResponse(path, request, outputStream);
    }

    private void login(Http11Request request, OutputStream outputStream) throws IOException {
        Optional<Http11Cookie> sessionCookie = request.findSessionCookie();
        if (sessionCookie.isEmpty()) {
            return;
        }

        String sessionId = sessionCookie.get().value();
        Session session = SESSION_MANAGER.findSession(sessionId);
        if (session == null) { // 쿠키를 만료시켜야 함
            SESSION_MANAGER.add(new Session(sessionId));
            session = SESSION_MANAGER.findSession(sessionId);
        }

        if (session.hasAttribute("user")) { // 로그인 되어있음
            sendRedirect("/index.html", outputStream);
            return;
        }

        Http11Method http11Method = request.method();
        if (http11Method.equals(Http11Method.GET)) { // 페이지 요청임
            return;
        }

        LinkedHashMap<String, String> requestBody = request.body();
        String account = requestBody.getOrDefault("account", "");
        String password = requestBody.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        boolean loginSuccess = user.isPresent() && user.get().checkPassword(password);
        if (loginSuccess) {
            session.setAttribute("user", user);
            sendRedirect("/index.html", outputStream);
            return;
        }

        sendRedirect("/401.html", outputStream);
    }

    private void sendRedirect(String uri, OutputStream outputStream) throws IOException {
        Http11Response response = Http11Response.found(uri);
        outputStream.write(response.toBytes());
        outputStream.flush();
    }

    private void register(Http11Request request, OutputStream outputStream) throws IOException {
        Optional<Http11Cookie> sessionCookie = request.findSessionCookie();
        if (sessionCookie.isEmpty()) {
            return;
        }

        String sessionId = sessionCookie.get().value();
        Session session = SESSION_MANAGER.findSession(sessionId);
        if (session == null) { // 쿠키를 만료시켜야 함
            SESSION_MANAGER.add(new Session(sessionId));
            session = SESSION_MANAGER.findSession(sessionId);
        }

        if (session.hasAttribute("user")) { // 로그인 되어있음
            sendRedirect("/index.html", outputStream);
            return;
        }

        Http11Method http11Method = request.method();
        if (http11Method.equals(Http11Method.GET)) {
            return;
        }
        LinkedHashMap<String, String> requestBody = request.body();
        String account = requestBody.getOrDefault("account", "");
        String password = requestBody.getOrDefault("password", "");
        String email = requestBody.getOrDefault("email", "");

        InMemoryUserRepository.save(new User(account, password, email));

        sendRedirect("/index.html", outputStream);
    }

    private void sendStaticResourceResponse(Path path, Http11Request request, OutputStream outputStream)
            throws IOException {
        Http11Response response = Http11Response.ok(new ArrayList<>(), new ArrayList<>(), path);
        if (!request.hasSessionCookie() && response.isHtml()) {
            Http11Cookie sessionCookie = Http11Cookie.sessionCookie();
            SESSION_MANAGER.add(new Session(sessionCookie.value()));
            response = Http11Response.ok(new ArrayList<>(), List.of(sessionCookie), path);
        }
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
