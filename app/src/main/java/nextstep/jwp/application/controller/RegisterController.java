package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.domain.User;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.body.HttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.mvc.controller.AbstractController;

public class RegisterController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        HttpRequestBody<?> body = request.body();

        String account = (String) body.getAttribute("account");
        String email = (String) body.getAttribute("email");
        String password = (String) body.getAttribute("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setStatus(HttpStatus.FOUND);
        response.headers().setLocation("/login");
    }
}
