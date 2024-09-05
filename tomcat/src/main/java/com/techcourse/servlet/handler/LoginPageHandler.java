package com.techcourse.servlet.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.servlet.Handler;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(LoginPageHandler.class);

    private static final HttpMethod METHOD = HttpMethod.GET;
    private static final String PATH = "/login";

    @Override
    public boolean support(HttpRequest request) {
        return request.getPath().equals(PATH)
                && request.getMethod() == METHOD;
    }

    @Override
    public View handle(HttpRequest request) {
        Optional<String> account = request.getQueryParameter("account");
        Optional<String> password = request.getQueryParameter("password");
        if (account.isPresent() && password.isPresent()) {
            leaveUserLog(account.get(), password.get());
        }

        return View.createByStaticResource("/login.html");
    }

    private void leaveUserLog(String account, String password) {
        InMemoryUserRepository.findByAccountAndPassword(account, password)
                .ifPresentOrElse(
                        user -> log.info("user : {}", user),
                        () -> log.info("해당 유저가 존재하지 않습니다"));
    }
}
