package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.RequestParam;
import nextstep.jwp.http.View;
import nextstep.jwp.http.ViewResolver;
import nextstep.jwp.http.entity.HttpSession;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        View view = ViewResolver.resolveView("login");
        view.render(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestParam params = RequestParam.of(httpRequest.body());
        String account = params.get("account");
        String password = params.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 계정입니다."));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("잘못된 패스워드입니다.");
        }

        final HttpSession httpSession = httpRequest.httpSession();
        httpSession.setAttribute("user", user);

        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setLocation("/index.html");
    }
}
