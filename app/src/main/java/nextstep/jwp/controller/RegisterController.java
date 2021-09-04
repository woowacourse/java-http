package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.ok(httpRequest.getRequestUrl() + ".html");
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpBody httpBody = httpRequest.getBody();
        String account = httpBody.getBodyParams("account");
        String email = httpBody.getBodyParams("email");
        String password = httpBody.getBodyParams("password");
        User user = new User(InMemoryUserRepository.assignId(), account, password, email);
        InMemoryUserRepository.save(user);

        httpResponse.redirect("/index.html");
    }
}
