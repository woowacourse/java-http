package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Objects;

import nextstep.jwp.HttpSession;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public class LoginController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isGet()) {

            if (isLoginStatus(request.getSession())) {
                response.redirect("/index.html");
                return;
            }
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

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.redirect("/index.html");
        }
    }

    private boolean isLoginStatus(HttpSession session) {
        Object user = session.getAttributes("user");
        return Objects.nonNull(user);
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

