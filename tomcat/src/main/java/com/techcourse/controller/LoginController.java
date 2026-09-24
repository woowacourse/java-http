package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final StaticResourceController page =
            new StaticResourceController("static/login.html", "text/html;charset=utf-8");

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if ("GET".equals(request.method())) {
            final HttpSession session = request.session();
            if (isLoggedIn(session)) {
                response.redirect("/index.html");
                return;
            }
            if (session != null) {
                request.refreshSessionCookie();
            }
        }
        page.render(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final String account = request.parameter("account");
        final String password = request.parameter("password");
        final User user = account == null || password == null
                ? null
                : InMemoryUserRepository.findByAccount(account)
                        .filter(candidate -> candidate.checkPassword(password))
                        .orElse(null);

        if (user != null) {
            request.renewSession().setAttribute("user", user);
            log.info("회원 조회 성공: {}", account);
        }
        response.redirect(user != null ? "/index.html" : "/401.html");
    }

    private boolean isLoggedIn(HttpSession session) {
        if (session == null) {
            return false;
        }
        try {
            return session.getAttribute("user") instanceof User;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
