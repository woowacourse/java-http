package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.StaticResourceBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        if (request.getSession(false) != null) {
            return HttpResponse.redirect("/");
        }
        return HttpResponse.ok(StaticResourceBody.from("/login.html"));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody body = request.body();

        Optional<String> account = body.get("account");
        Optional<String> password = body.get("password");

        if (account.isEmpty() || password.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account.get())
                .filter(found -> found.checkPassword(password.get()));

        if (user.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }
        log.info("login user: {}", user.get());

        Session session = request.getSession(true);
        session.setAttribute("user", user.get());
        return HttpResponse.redirect("/index.html")
                .addCookie("JSESSIONID", session.getId());
    }
}
