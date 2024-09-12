package com.techcourse.service;

import com.techcourse.controller.LoginController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Cookie;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = SessionManager.getInstance();

    public boolean existsUser(HttpRequest request) {
        String sessionId = request.getSessionId();
        Session session = sessionManager.findSession(sessionId);
        return session.isPresent() && session.getAttribute("user") != null;
    }

    public boolean login(HttpRequest request, HttpResponse response) {
        Map<String, String> params = request.getBody();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get("account"));
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        if (user.checkPassword(params.get("password"))) {
            Session session = saveSession(user);
            log.info("{}", user);
            response.setCookie(new Cookie(Map.of("JSESSIONID", session.getId())));
            return true;
        }
        return false;
    }

    private Session saveSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);
        return session;
    }

    public void register(HttpRequest request) {
        Map<String, String> requestBody = request.getBody();
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);
    }
}
