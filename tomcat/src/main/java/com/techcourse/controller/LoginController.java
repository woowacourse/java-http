package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpParameters;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        HttpParameters parameters = request.getParameters();
        String account = parameters.getFirst("account");
        String password = parameters.getFirst("password");

        if (account == null || password == null) {
            response.redirect("/login.html");
            return;
        }

        boolean success = InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);

        if (success) {
            log.info("Login OK - account {}", account);
            Session session = request.getSession(true);
            InMemoryUserRepository.findByAccount(account).ifPresent(u -> session.setAttribute("user", u));

            String jsessionId = session.getId();
            String setCookie = HttpCookie.buildSetCookieHeader(jsessionId);
            response.addSetCookie(setCookie);

            response.redirect("/index.html");
            return;
        }

        log.info("Login FAILED - invalid password {}", account);
        response.redirect("/401.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (isLoggedIn(request)) {
            response.redirect("/index.html");
            return;
        }
        response.redirect("/login.html");
    }

    private boolean isLoggedIn(final HttpRequest request) {
        Session session = request.getSession(false);
        User loginUser = getUser(session);
        return loginUser != null;
    }

    private User getUser(Session session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("user");
    }
}
