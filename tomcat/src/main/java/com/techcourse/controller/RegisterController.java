package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.core.controller.AbstractController;
import org.apache.catalina.core.exception.InvalidRequestException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.tomcat.util.http.Cookie;
import org.apache.tomcat.util.http.request.HttpRequest;
import org.apache.tomcat.util.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    public RegisterController() {
        super("/register");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        validateRequestBody(request);

        String account = request.getBody("account");
        String email = request.getBody("email");
        String password = request.getBody("password");
        validateAlreadyAccountExistence(account);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        String sessionId = makeSession(user);
        response.setRedirection("/index.html");
        response.setCookie(Cookie.makeSessionCookie(sessionId));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Cookie cookie = request.getCookie(Cookie.SESSION_COOKIE_KEY);
        if (cookie == null) {
            response.setRedirection("/register.html");
            return;
        }

        Session session = SessionManager.findSession(cookie.getValue());
        if (session == null) {
            response.setRedirection("/register.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!isValidUser(user)) {
            response.setRedirection("/register.html");
            return;
        }

        response.setRedirection("/index.html");
    }

    private boolean isValidUser(User user) {
        return InMemoryUserRepository
                .findByAccount(user.getAccount())
                .isPresent();
    }

    private void validateAlreadyAccountExistence(String account) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new InvalidRequestException("이미 존재하는 유저입니다.");
        }
    }

    private String makeSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);
        return session.getId();
    }

    private void validateRequestBody(HttpRequest request) {
        String account = request.getBody("account");
        String email = request.getBody("email");
        String password = request.getBody("password");
        if (account == null || account.isEmpty() &&
                email == null || email.isEmpty() &&
                password == null || password.isEmpty()) {
            throw new InvalidRequestException("잘못된 회원가입 형식입니다.");
        }
    }
}
