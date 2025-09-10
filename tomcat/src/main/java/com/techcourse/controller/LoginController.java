package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Session session = request.getSession();
        if ("GET".equals(request.getMethod()) && session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        if ("POST".equals(request.getMethod())) {
            handleLogin(request, response);
            return;
        }

        response.sendRedirect("/login.html");
    }

    private void handleLogin(HttpRequest request, HttpResponse response) {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty() || !userOptional.get().checkPassword(password)) {
            log.info("로그인 실패: 아이디 또는 비밀번호 불일치");
            response.sendRedirect("/401.html");
            return;
        }
        User user = userOptional.get();
        log.info("로그인 성공: {}", user);
        Session session = request.getSession();
        session.setAttribute("user", user);
        response.sendRedirect("/index.html");
    }
}
