package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.constant.HttpMethod;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> bodyParams = request.getBody();

        String account = Objects.requireNonNull(bodyParams.get("account"), "계정이 입력되지 않았습니다.");
        String password = Objects.requireNonNull(bodyParams.get("password"), "비밀번호가 입력되지 않았습니다.");
        String email = Objects.requireNonNull(bodyParams.get("email"), "이메일이 입력되지 않았습니다.");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.addHeader("Location", "/index.html");
        response.statusCode(HttpStatus.REDIRECT);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.loadResource("/register.html");
    }
}
