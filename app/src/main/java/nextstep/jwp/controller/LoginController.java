package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.*;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.respond(request.getUri() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        RequestBody requestBody = request.getRequestBody();
        HttpResponse response = new HttpResponse();
        String account = requestBody.getParam("account");
        String password = requestBody.getParam("password");

        return postRespond(response, account, password);
    }

    private HttpResponse postRespond(HttpResponse response, String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return response.redirect("/400.html", HttpStatus.BAD_REQUEST);
        }
        if (!user.get().checkPassword(password)) {
            return response.redirect("/401.html", HttpStatus.UNAUTHORIZED);
        }
        return response.redirect("/index.html", HttpStatus.FOUND);
    }
}
