package nextstep.jwp.web.controller;

import static nextstep.jwp.http.HttpResponse.redirect;
import static nextstep.jwp.http.ViewResolver.resolveView;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.RequestParam;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected String doGet(HttpRequest httpRequest) throws IOException {
        return resolveView("register");
    }

    @Override
    protected String doPost(HttpRequest httpRequest) {
        RequestParam params = RequestParam.of(httpRequest.payload());
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        User user = new User(null, account, password, email);
        InMemoryUserRepository.save(user);

        return redirect("/index.html");
    }
}
