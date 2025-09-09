package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.contoller.AbstractController;
import org.apache.catalina.exception.Http4xxException;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::getPage);
        this.addCommand(HttpMethod.POST, this::postToRegister);
    }

    public String getPage(final Http11Request request, final Http11Response response) {
        return "/register";
    }

    public String postToRegister(final Http11Request request, final Http11Response response) {
        final String account = request.body().getValueByKey("account");
        final String password = request.body().getValueByKey("password");
        final String email = request.body().getValueByKey("email");
        if (account != null && !account.isBlank()) {
            if (InMemoryUserRepository.findByAccount(account).isPresent()) {
                throw new Http4xxException("이미 가입한 사용자입니다.", response, HttpStatus.BAD_REQUEST);
            }
            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            log.info("User created: {}", user);
            return "/index";
        }
        InMemoryUserRepository.save(null);
        return "/register";
    }
}
