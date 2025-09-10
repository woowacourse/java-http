package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        Map<String, List<String>> parameters = request.getParameters();
        String account = getFirst(parameters, "account");
        String password = getFirst(parameters, "password");

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

    private String getFirst(final Map<String, List<String>> map, final String key) {
        List<String> list = map.get(key);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.getFirst();
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
