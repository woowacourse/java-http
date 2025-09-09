package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::getPage);
        this.addCommand(HttpMethod.POST, this::postToLogin);
    }

    public String getPage(final Http11Request request, final Http11Response response) {
        return "/login";
    }

    public String postToLogin(final Http11Request request, final Http11Response response) {
        final String account = request.body().getValueByKey("account");
        final String password = request.body().getValueByKey("password");
        if (account != null && !account.isBlank()) {
            final Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isPresent() && user.get().checkPassword(password)) {
                log.info("User found: {}", user.get());
                response.setState(HttpStatus.Found);
                return "/index";
            }
            throw new UnauthorizedException(response);
        }
        return "/login";
    }
}
