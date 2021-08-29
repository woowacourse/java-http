package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class RegisterController {

    public void doPost(HttpRequest request, HttpResponse response) {
        HttpRequestBody body = request.body();

        String account = (String) body.getAttribute("account");
        String email = (String) body.getAttribute("email");
        String password = (String) body.getAttribute("password");

        User user = new User(account, email, password);
        InMemoryUserRepository.save(user);

        response.setStatus(HttpStatus.FOUND);
        response.headers().add("Location", "/login");
    }
}
