package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;
import org.apache.coyote.Processor;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final HttpRequest request = new HttpRequest(reader);
            final HttpResponse response = new HttpResponse(request.getProtocolVersion());

            String path = request.getPath();
            response.setPath(request.getPath());
            String jSessionId = "";

            if (request.hasHeader("Cookie")) {
                HttpCookie httpCookie = new HttpCookie(request.getHeaderValue("Cookie"));
                if (hasJSessionId(httpCookie)) {
                    jSessionId = httpCookie.get("JSESSIONID");
                }
            }

            boolean hasJSessionId = !jSessionId.isEmpty();

            final SessionManager manager = SessionManager.getInstance();

            if (path.equals("/login") && request.getMethod().equals("GET")) {
                if (hasJSessionId) {
                    log.info("쿠키 존재!");
                    log.info("session: {}", manager.findSession(jSessionId));
                    if (manager.findSession(jSessionId) != null) {
                        log.info("세션 존재!");
                        response.setStatus("302 FOUND");
                        response.addHeader("Location", "/index.html");
                    }
                }
            }

            if (request.hasBody()) {
                if (path.equals("/login")) {
                    login(request, response, manager, jSessionId);
                } else {
                    User user = new User(request.getBodyValue("account"), request.getBodyValue("password"),
                            request.getBodyValue("email"));
                    log.info("user: {}", user);
                    InMemoryUserRepository.save(user);
                    response.setStatus("302 FOUND");
                    response.addHeader("Location", "/index.html");
                }
            }

            log.info("uri: {}", path);

            String responseValue = response.response();

            outputStream.write(responseValue.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void login(HttpRequest request, HttpResponse response, SessionManager manager, String jSessionId) {
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");
        if (userMatching(account, password)) {
            if (manager.findSession(jSessionId) == null) {
                addSession(account, response, manager);
            }
            response.addHeader("Location", "/index.html");
        } else {
            response.addHeader("Location", "/401.html");
        }
        response.setStatus("302 FOUND");
    }

    private void addSession(String account, HttpResponse response, SessionManager manager) {
        String jSessionId = UUID.randomUUID().toString();
        Session session = new Session(jSessionId);
        boolean isPresent = InMemoryUserRepository.findByAccount(account).isPresent();
        if (isPresent) {
            User user = InMemoryUserRepository.findByAccount(account).get();
            session.setAttribute("user", user);
            manager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + jSessionId + " ");
        }
    }

    private boolean hasJSessionId(HttpCookie httpCookie) {
        return httpCookie.containsKey("JSESSIONID");
    }

    private boolean userMatching(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
