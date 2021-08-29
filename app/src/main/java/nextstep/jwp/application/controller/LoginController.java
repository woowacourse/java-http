package nextstep.jwp.application.controller;

import java.util.Optional;
import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.domain.User;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.body.FormDataHttpRequestBody;
import nextstep.jwp.web.http.request.body.HttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.mvc.controller.AbstractController;

public class LoginController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        HttpRequestBody<String> body = (FormDataHttpRequestBody) request.body();

        String account = body.getAttribute("account");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            response.setStatus(HttpStatus.FOUND);
            response.headers().add("Location", "/index");
        }
    }
}
