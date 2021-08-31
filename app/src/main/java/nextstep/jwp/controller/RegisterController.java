package nextstep.jwp.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, URISyntaxException {
        httpResponse.ok(httpRequest.getRequestUrl() + ".html");
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, URISyntaxException {
        HttpBody httpBody = httpRequest.getBody();
        String account = httpBody.getBodyParams("account");
        String email = httpBody.getBodyParams("email");
        String password = httpBody.getBodyParams("password");
        User user = new User(InMemoryUserRepository.assignId(), account, password, email);
        InMemoryUserRepository.save(user);

        httpResponse.redirect("/index.html");
    }
}
