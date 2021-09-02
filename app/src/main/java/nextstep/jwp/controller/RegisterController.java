package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public class RegisterController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isGet()) {
            response.forward("/register.html");
        }

        if (request.isPost()) {
            String account = request.getRequestBodyParam("account");
            String password = request.getRequestBodyParam("password");
            String email = request.getRequestBodyParam("email");

            User user = new User(2, account, password, email);
            InMemoryUserRepository.save(user);

            response.redirect("/index.html");
        }
    }
}
