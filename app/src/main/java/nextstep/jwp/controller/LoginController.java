package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class LoginController {

    public void login(HttpRequest request, HttpResponse httpResponse) {
        //InMemoryUserRepository.findByAccount();
        request.body();
    }
}
