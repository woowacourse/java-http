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
        HttpResponse response = new HttpResponse.Builder()
                .outputStream(httpResponse.getOutputStream())
                .status(HttpStatus.OK_200)
                .body(createBody("/register.html"))
                .path(httpRequest.getPath())
                .build();
        response.forward();
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
            HttpResponse response = new HttpResponse.Builder()
                    .outputStream(httpResponse.getOutputStream())
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .body(createBody("/401.html"))
                    .path(httpRequest.getPath())
                    .build();
            response.forward();
            return;
        }
        InMemoryUserRepository.save(user);
        HttpResponse response = new HttpResponse.Builder()
                .outputStream(httpResponse.getOutputStream())
                .status(HttpStatus.FOUND_302)
                .redirectUrl("/index.html")
                .build();
        response.forward();
    }
}
