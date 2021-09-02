package nextstep.jwp.controller;

import java.io.IOException;
import java.util.UUID;

import nextstep.jwp.HttpSession;
import nextstep.jwp.HttpSessions;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public class LoginController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isGet()) {
            response.forward("/login.html");
        }

        if (request.isPost()) {
            String account = request.getRequestBodyParam("account");
            String password = request.getRequestBodyParam("password");

            User user = getUser(response, account);

            if (!user.checkPassword(password)) {
                response.redirect("/401.html");
                return;
            }

            response.redirect("/index.html");
        }

    }

    private User getUser(HttpResponse response, String account) throws IOException {
        User user = null;
        try {
            user = InMemoryUserRepository.findByAccount(account);
        } catch (IllegalArgumentException e) {
            response.redirect("/401.html");
        }
        return user;
    }
}

