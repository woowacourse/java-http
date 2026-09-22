package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;

public class LoginController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        Session session = request.getSession();

        boolean loggedIn = session.getAttribute("user") != null;
        if (loggedIn) {
            response.sendRedirect("/index.html");
            return;
        }
        response.forward("/login.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        if (request.getParamSize() < 2) {
            log.debug("로그인 파라미터가 부족합니다.");
            response.forward("/login.html");
            return;
        }
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> {
                            if (!user.checkPassword(password)) {
                                log.debug("비밀번호 불일치: {}", account);
                                response.sendRedirect("/401.html");
                                return;
                            }

                            Session session = request.getSession();
                            session.setAttribute("user", user);
                            log.debug("로그인 성공: {}", account);
                            response.sendRedirect("/index.html");
                        },
                        () -> {
                            log.debug("존재하지 않는 계정: {}", account);
                            response.sendRedirect("/401.html");
                        }
                );
    }
}
