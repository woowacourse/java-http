package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.Cookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public LoginController() {
        super("/login");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Cookie cookie = request.getCookie(Cookie.SESSION_COOKIE_KEY);
        Session session = SessionManager.findSession(cookie.getValue());
        if (session != null && isExistUser(session.getUser().getAccount())) {
            response.setRedirection("/index.html");
            return;
        }
        response.setRedirection("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getBody("account");
        String password = request.getBody("password");

        Optional<User> user = getUser(account);
        boolean isAuthenticated = isAuthenticatedUser(user, password);
        if (!isAuthenticated) {
            response.setRedirection("/401.html");
            return;
        }

        String sessionId = makeSession(user.get());
        response.setRedirection("/index.html");
        response.setCookie(Cookie.makeSessionCookie(sessionId));
    }

    private Optional<User> getUser(String account) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        user.ifPresent(value -> log.info("user : {}", value.toString()));
        return user;
    }

    private boolean isExistUser(String account) {
        return getUser(account).isPresent();
    }

    private boolean isAuthenticatedUser(Optional<User> user, String password) {
        return user.isPresent() && user.get().checkPassword(password);
    }

    private String makeSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        SessionManager.add(new Session(sessionId, user));
        return sessionId;
    }
}
