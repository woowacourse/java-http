package nextstep.jwp.controller;

import java.io.IOException;

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
            String[] splitBody = request.getRequestBody().split("&");
            String account = splitBody[0].split("=")[1];
            String password = splitBody[1].split("=")[1];
            String email = splitBody[2].split("=")[1];

            User user = new User(2, account, password, email);
            InMemoryUserRepository.save(user);

            response.redirect("/index.html");
        }
    }
}
