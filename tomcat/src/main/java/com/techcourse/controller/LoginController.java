package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final String LOGIN_USER = "user";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(HttpRequest request, Session session) throws IOException {
        User loginUser = (User) session.getAttribute(LOGIN_USER);

        if (loginUser != null) {
            return redirectResponse(request, session, "/index.html");
        }

        return resourceResponse(request, session, "static/login.html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request, Session session) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        if (!validateInput(account) || !validateInput(password)) {
            return redirectResponse(request, session, "/401.html");
        }

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (loginUser.isEmpty()) {
            return redirectResponse(request, session, "/401.html");
        }

        User user = loginUser.get();
        session.setAttribute(LOGIN_USER, user);
        log.info("로그인 성공 ! : {}", user.getAccount());

        return redirectResponse(request, session, "/index.html");
    }

    private boolean validateInput(String input) {
        return input != null && !input.isBlank();
    }
}

