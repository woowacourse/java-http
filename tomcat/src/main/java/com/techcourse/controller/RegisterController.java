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

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        HttpParameters parameters = request.getParameters();
        String account = getFirst(parameters, "account");
        String email = getFirst(parameters, "email");
        String password = getFirst(parameters, "password");

        if (account != null && email != null && password != null) {
            InMemoryUserRepository.save(new User(account, password, email));
            log.info("Register OK - account {}", account);

            Session session = request.getSession(true);
            session.setAttribute("user", new User(account, password, email));
            String setCookie = HttpCookie.buildSetCookieHeader(session.getId());
            response.addSetCookie(setCookie);

            response.redirect("/index.html");
            return;
        }

        response.redirect("/register.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.redirect("/register.html");
    }

    private String getFirst(final HttpParameters map, final String key) {
        return map.getFirst(key);
    }
}
