package nextstep.jwp.application.controller;

import java.util.Optional;
import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.domain.User;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.body.HttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.mvc.controller.AbstractController;

public class LoginController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        HttpRequestBody<?> body = request.body();

        String account = (String) body.getAttribute("account");
        Optional<User> foundAccount = InMemoryUserRepository.findByAccount(account);
        if (foundAccount.isPresent()) {
            boolean pass = foundAccount.get().checkPassword((String) body.getAttribute("password"));
            if (pass) {
                response.setStatus(HttpStatus.FOUND);
                response.headers().add("Location", "/index");
            }
        }
    }
}
