package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.DispatchResult;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RegisterRequest;
import org.apache.coyote.http11.WebController;

@WebController(path = "/register")
public class RegisterController extends AbstractController {

    @Override
    protected DispatchResult doPost(HttpRequest request, HttpResponse response) throws Exception {
        final RegisterRequest registerRequest = RegisterRequest.from(request.requestBody());
        final User newUser =
            new User(registerRequest.account(), registerRequest.password(),
                registerRequest.email());
        if (isAlreadyRegistered(registerRequest.account())) {
            log.info("이미 존재하는 회원입니다 (account: {})", registerRequest.account());
            return DispatchResult.forward(HttpStatus.OK, "/register.html");
        }
        InMemoryUserRepository.save(newUser);
        log.info("register: {}", newUser);

        return DispatchResult.redirect(HttpStatus.SEE_OTHER, "/index");
    }

    private boolean isAlreadyRegistered(final String account) {
        return InMemoryUserRepository.findByAccount(account)
            .isPresent();
    }

    @Override
    protected DispatchResult doGet(HttpRequest request, HttpResponse response) throws Exception {
        return DispatchResult.forward(HttpStatus.OK, "/register.html");
    }
}
