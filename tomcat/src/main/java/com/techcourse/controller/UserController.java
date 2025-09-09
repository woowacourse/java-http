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

public class UserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::getToLogin);
        this.addCommand(HttpMethod.POST, this::toPost);
    }

    public String getToLogin(final Http11Request request, final Http11Response response) {
        final String account = request.parseQuery().get("account");
        final String password = request.parseQuery().get("password");
        if (account != null && !account.isBlank()) {
            final Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isPresent() && user.get().checkPassword(password)) {
                log.info("User found: {}", user.get());
                response.setState(HttpStatus.Found);
                return "/index.html";
            }
            throw new UnauthorizedException(response);
        }
        return "/login.html";
    }

    public String toPost(final Http11Request request, final Http11Response response) {
        //Todo: step2 기능 추가 예정 [2025-09-05 16:26:11]
        InMemoryUserRepository.save(null);
        return request.parseResourcePath();
    }
}
