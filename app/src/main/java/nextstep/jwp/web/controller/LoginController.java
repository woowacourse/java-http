package nextstep.jwp.web.controller;

import static nextstep.jwp.http.HttpResponse.found;

import java.io.IOException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.RequestParam;
import nextstep.jwp.http.ViewResolver;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.model.User;

public class LoginController extends AbstractController {

    @Override
    protected String doGet(HttpRequest httpRequest) throws IOException {
        return ViewResolver.resolveView("login");
    }

    @Override
    protected String doPost(HttpRequest httpRequest) {
        RequestParam params = RequestParam.of(httpRequest.payload());
        String account = params.get("account");
        String password = params.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 계정입니다."));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("잘못된 패스워드입니다.");
        }

        return found("/index.html");
    }
}
