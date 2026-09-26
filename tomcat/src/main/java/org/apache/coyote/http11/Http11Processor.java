package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String LOGIN_PATH = "/login";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;
    private final RequestMapping requestMapping;

    public Http11Processor(
            Socket connection,
            Manager sessionManager,
            RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.sessionManager = sessionManager;
        this.requestMapping = requestMapping;
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
            HttpRequest request = HttpRequest.readFrom(inputStream);
            if (request == null) {
                return;
            }
            Optional<Session> existingSession = request.findCookie("JSESSIONID")
                    .flatMap(sessionManager::findSession);
            Session session = existingSession.orElseGet(() -> {
                Session newSession = new Session(UUID.randomUUID().toString());
                sessionManager.add(newSession);
                return newSession;
            });
            HttpResponse response = route(request, session);
            if (existingSession.isEmpty() && !response.hasHeader("Set-Cookie")) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            }
            outputStream.write(response.toByteArray());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse route(HttpRequest request, Session session)
            throws Exception {
        if (request.matches("POST", LOGIN_PATH)) {
            return login(request, session);
        }
        if (request.matches("GET", LOGIN_PATH) && session.getAttribute("user") instanceof User) {
            HttpResponse response = new HttpResponse();
            response.sendRedirect("/index.html");
            return response;
        }

        HttpResponse response = new HttpResponse();

        Controller controller = requestMapping.getController(request);
        controller.service(request, response);

        return response;
    }

    private HttpResponse login(HttpRequest request, Session session) {
        String account = request.findFormParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
        String password = request.findFormParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        user.ifPresent(value -> log.info("user : {}", value));
        Optional<User> authenticatedUser = user.filter(value -> value.checkPassword(password));
        if (authenticatedUser.isEmpty()) {
            HttpResponse response = new HttpResponse();
            response.sendRedirect("/401.html");
            return response;
        }

        Session renewedSession = new Session(UUID.randomUUID().toString());
        renewedSession.setAttribute("user", authenticatedUser.get());
        sessionManager.remove(session.getId());
        sessionManager.add(renewedSession);

        HttpResponse response = new HttpResponse();
        response.addHeader("Set-Cookie", "JSESSIONID=" + renewedSession.getId());
        response.sendRedirect("/index.html");
        return response;
    }
}
