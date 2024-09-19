package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Status;
import org.apache.coyote.http11.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String sessionId = request.getCookie(HttpResponse.SESSION_ID_NAME);
        if (sessionManager.hasSession(sessionId)) {
            response.generateResponse("/index.html", Status.FOUND);
            return;
        }
        response.generateResponse("static" + request.getPath(), Status.OK);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<Map<String, String>> parsed = request.parseQueryString();
        Map<String, String> parsedQueryString = parsed.orElseThrow(
                () -> new NoSuchElementException("invalid query string")
        );
        login(parsedQueryString, response);
    }

    private void login(Map<String, String> parsedQueryString, HttpResponse response) throws IOException {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(parsedQueryString.get("account"));
        if (optionalUser.isEmpty()) {
            response.generateResponse("/401.html", Status.FOUND);
            return;
        }
        User user = optionalUser.get();
        if (user.checkPassword(parsedQueryString.get("password"))) {
            log.info("로그인 성공! 아이디 : {}", optionalUser.get().getAccount());
            final var session = getSession();
            session.setAttribute("user", user);
            response.generateResponse("/index.html", Status.FOUND, session.getId());
            return;
        }
        response.generateResponse("/401.html", Status.FOUND);
    }

    public Session getSession() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        sessionManager.add(session);
        return session;
    }
}
