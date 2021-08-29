package nextstep.jwp.controller;


import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;


public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody body = request.getRequestBody();

        String account = body.getParam("account");
        String password = body.getParam("password");
        String email = body.getParam("email");

        User user = new User(InMemoryUserRepository.getUserId(), account, password, email);
        InMemoryUserRepository.save(user);
        log.debug("{} 님이 회원가입 하였습니다.", user.getAccount());

        response.redirect("/index.html");
    }
}
