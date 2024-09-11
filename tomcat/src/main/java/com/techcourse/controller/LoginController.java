package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    public LoginController() {
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (isLogin(request, response)) {
            return;
        }
        response.setResourceName("/login.html");
        response.ok();
    }

    private boolean isLogin(HttpRequest request, HttpResponse response) {
        Session session = request.getSession();
        if (session == null) {
            return false;
        }
        User user = (User) session.getAttribute("user");
        if (user != null && validateUser(user)) {
            response.found();
            response.redirectPage("/index.html");
            return true;
        }
        return false;
    }

    private boolean validateUser(User expected) {
        Optional<User> found = InMemoryUserRepository.findByAccount(expected.getAccount());
        if (found.isEmpty()) {
            return false;
        }
        User actual = found.get();
        return actual.equals(expected);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> values = BodyParser.parseValues(request.getBody());
        String account = values.get("account");
        String password = values.get("password");
        if (account == null || password == null) {
            failLogin(response);
            return;
        }
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> successLogin(user, request, response),
                        () -> failLogin(response)
                );
        response.found();
    }

    private void successLogin(User user, HttpRequest request, HttpResponse response) {
        LOGGER.info(user.toString());
        Session session = request.getSession();
        session.setAttribute("user", user);
        response.setHttpCookie(HttpCookie.ofJSessionId(session.getId()));
        response.redirectPage("/index.html");
    }

    private void failLogin(HttpResponse response) {
        response.redirectPage("/401.html");
    }
}
