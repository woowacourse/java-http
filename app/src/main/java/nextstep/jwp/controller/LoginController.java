package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController {

    public void login(HttpRequest request, HttpResponse httpResponse) {
        request.body();
        request.body();
    }

    public void doPost(HttpRequest request, HttpResponse response) {
        HttpRequestBody body = request.body();

        String account = (String) body.getAttribute("account");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            response.setStatus(HttpStatus.FOUND);
            response.headers().add("Location", "/index");
        }

    }
}
