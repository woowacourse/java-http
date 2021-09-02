package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class RegisterController extends AbstractController {
    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.OK_200);
        httpResponse.setBody(createBody("/register.html"));
        httpResponse.setPath(httpRequest.getPath());
        httpResponse.forward();
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> params = httpRequest.getParams();
        User user = new User(InMemoryUserRepository.size() + 1L,
                params.get("account"),
                params.get("password"),
                params.get("email"));

        Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));
        if (account.isPresent()) {
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED_401);
            httpResponse.setBody(createBody("/401.html"));
            httpResponse.setPath(httpRequest.getPath());
            httpResponse.forward();

        } else {
            InMemoryUserRepository.save(user);
            httpResponse.setStatus(HttpStatus.FOUND_302);
            httpResponse.setRedirectUrl("/index.html");
            httpResponse.redirect();
        }
    }
}
