package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final Manager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        HttpCookie httpCookie = request.getCookies();
        String sessionId = httpCookie.getSessionId();
        if (httpCookie.hasSession() && sessionManager.findSession(sessionId) != null) {
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getBodyParam("account");
        String password = request.getBodyParam("password");
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty() || !userOptional.get().checkPassword(password)) {
            log.info("로그인 실패: 아이디 또는 비밀번호 불일치");
            response.sendRedirect("/401.html");
            return;
        }
        User user = userOptional.get();
        log.info("로그인 성공: {}", user);
        Session session = response.addSession(sessionManager);
        session.setAttribute("user", user);
        response.sendRedirect("/index.html");
    }
}
