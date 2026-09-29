package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(
            HttpRequest request,
            HttpResponse response
    ) throws IOException {
        Session session = request.getSession();

        if (session != null
                && session.getAttribute("user") != null) {
            response.redirect("/index.html");
            return;
        }

        response.fromResource("login.html");
    }

    @Override
    protected void doPost(
            HttpRequest request,
            HttpResponse response
    ) throws IOException {
        Optional<User> user = authenticate(request);

        if (user.isEmpty()) {
            response.redirect("/401.html");
            return;
        }

        Session session = request.getSession();

        session.setAttribute("user", user.get());
        response.redirect("/index.html");
    }

    private Optional<User> authenticate(HttpRequest request) {
        String account =
                request.getBodyValue("account");

        String password =
                request.getBodyValue("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository
                .findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
