package nextstep.jwp.controller;

import java.io.IOException;

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
            String[] splitBody = request.getRequestBody().split("&");
            String account = splitBody[0].split("=")[1];
            String password = splitBody[1].split("=")[1];

            User user = InMemoryUserRepository.findByAccount(account)
                                              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

            if (!user.checkPassword(password)) {
                response.redirect("/401.html");
                return;
            }
            response.redirect("/index.html");
        }
    }
}
