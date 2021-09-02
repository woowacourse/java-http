package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class LoginController extends AbstractController {
    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.OK_200);
        httpResponse.setBody(createBody("/login.html"));
        httpResponse.setPath(httpRequest.getPath());
        httpResponse.forward();
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> params = httpRequest.getParams();
        Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));

        if (account.isPresent() && account.get().checkPassword(params.get("password"))) {
            httpResponse.setStatus(HttpStatus.FOUND_302);
            httpResponse.setRedirectUrl("/index.html");
            httpResponse.redirect();
        } else {
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED_401);
            httpResponse.setBody(createBody("/401.html"));
            httpResponse.setPath(httpRequest.getPath());
            httpResponse.forward();
        }
    }
}
