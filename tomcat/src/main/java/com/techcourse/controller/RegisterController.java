package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RegisterRequest;
import org.apache.coyote.http11.StatusLine;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final RegisterRequest registerRequest = RegisterRequest.from(request.requestBody());
        final User newUser =
            new User(registerRequest.account(), registerRequest.password(),
                registerRequest.email());
        InMemoryUserRepository.save(newUser);
        log.info("register: {}", newUser);

        response.addStatusLine(StatusLine.http11(HttpStatus.SEE_OTHER));
        response.sendRedirect("/index");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.addStatusLine(StatusLine.http11(HttpStatus.OK));
        response.forward("/register.html");
    }
}
