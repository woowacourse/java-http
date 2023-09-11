package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) {
        final String account = request.getParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("계정 입력이 잘못되었습니다."));
        final String password = request.getParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("비밀번호 입력이 잘못되었습니다."));
        final String email = request.getParameter("email")
                .orElseThrow(() -> new IllegalArgumentException("이메일 입력이 잘못되었습니다."));

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.redirect("/login");
    }

    @Override
    protected void doGet(final Request request, final Response response) {
        response.writeStaticResource("/register.html");
    }
}
