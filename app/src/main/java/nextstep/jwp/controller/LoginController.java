package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.domain.User;
import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.request.RequestBody;
import nextstep.jwp.domain.response.HttpResponse;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.respond(request.getUri() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        HttpResponse response = new HttpResponse();
        String account = requestBody.getParam("account");
        String password = requestBody.getParam("password");

        return postRespond(response, account, password);
    }

    private HttpResponse postRespond(HttpResponse response, String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return response.redirect("/400.html");
        }
        if (!user.get().checkPassword(password)) {
            return response.redirect("/401.html");
        }
        return response.redirect("/index.html");
    }
}
