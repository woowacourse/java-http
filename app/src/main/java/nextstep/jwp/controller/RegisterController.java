package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.*;

import java.io.IOException;
import java.util.Optional;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.respond(request.getUri() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        HttpResponse response = new HttpResponse();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.getParam("account"));
        if (user.isPresent()) {
            return response.redirect("/400.html", HttpStatus.BAD_REQUEST);
        }
        int latest = InMemoryUserRepository.getLatestId();
        User signupUser = new User(latest++, requestBody.getParam("account"), requestBody.getParam("password"), requestBody.getParam("email"));
        InMemoryUserRepository.save(signupUser);
        return response.redirect("/index.html", HttpStatus.FOUND);
    }
}
