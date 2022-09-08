package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.catalina.handler.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return new HttpResponse.Builder(request).ok()
            .messageBody(getStaticResource(request.getUrl())).build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");
        final String email = request.getQueryValue("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! 아이디: {}", user.getAccount());

        return new HttpResponse.Builder(request)
            .redirect()
            .location("index.html")
            .build();
    }

    private String getStaticResource(URL url) {
        try {
            return Files.readString(new File(Objects.requireNonNull(url)
                .getFile())
                .toPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("No such resource");
        }
    }
}
