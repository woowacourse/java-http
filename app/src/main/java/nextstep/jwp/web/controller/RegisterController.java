package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.RequestParam;
import nextstep.jwp.http.View;
import nextstep.jwp.http.ViewResolver;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        View view = ViewResolver.resolveView("register");
        view.render(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestParam params = RequestParam.of(httpRequest.body());
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new BadRequestException("이미 존재하는 계정입니다.");
        }

        User user = new User(null, account, password, email);
        InMemoryUserRepository.save(user);

        httpResponse.redirect("/index.html");
    }
}
