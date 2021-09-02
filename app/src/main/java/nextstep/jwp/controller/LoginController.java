package nextstep.jwp.controller;

import java.io.IOException;
import java.util.UUID;

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

            User user = InMemoryUserRepository.findByAccount(account)
                                              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

            if (!user.checkPassword(password)) {
                response.redirect("/401.html");
                return;
            }

            if (request.hasNoSessionId()) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + String.valueOf(UUID.randomUUID()));
            }
            response.redirect("/index.html");
        }

    }
}

