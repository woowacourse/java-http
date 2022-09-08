package nextstep.jwp.controller;

import org.apache.catalina.handler.AbstractController;
import org.apache.catalina.handler.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return ViewResolver.render(request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");
        final String email = request.getQueryValue("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! 아이디: {}", user.getAccount());

        return new HttpResponse.Builder(request)
            .redirect()
            .location("index")
            .build();
    }
}
