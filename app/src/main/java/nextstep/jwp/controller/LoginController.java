package nextstep.jwp.controller;


import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        RequestBody body = request.getRequestBody();

        String account = body.getParam("account");

        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. 회원가입을 해주세요"));
        if (user.checkPassword(body.getParam("password"))) {
            log.debug("{} 님이 로그인 하였습니다.", user.getAccount());
            response.redirect("/index.html");
            return;
        }
        log.debug("아이디나 비밀번호가 틀립니다.");
        response.exception("/401.html");

    }
}
