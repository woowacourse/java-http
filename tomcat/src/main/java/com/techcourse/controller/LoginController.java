package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.core.controller.AbstractController;
import org.apache.catalina.core.exception.UnauthorizedException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.apache.tomcat.util.http.Cookie;
import org.apache.tomcat.util.http.request.HttpRequest;
import org.apache.tomcat.util.http.response.HttpResponse;
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
        if (cookie == null) {
            response.setRedirection("/login.html");
            return;
        }

        Session session = SessionManager.findSession(cookie.getValue());
        if (session == null) {
            response.setRedirection("/login.html");
            return;
        }

        if (!isExistUser(session.getUser().getAccount())) {
            response.setRedirection("/login.html");
            return;
        }

        response.setRedirection("/index.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getBody("account");
        String password = request.getBody("password");

        Optional<User> user = getUser(account);
        boolean isAuthenticated = isAuthenticatedUser(user, password);
        if (!isAuthenticated) {
            throw new UnauthorizedException("로그인에 실패했습니다.");
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
