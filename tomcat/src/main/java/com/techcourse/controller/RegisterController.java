package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.StaticResource;
import org.apache.catalina.StaticResources;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<StaticResource> registerPage = StaticResources.find("/register.html");
        if (registerPage.isEmpty()) {
            response.setError(HttpStatus.NOT_FOUND);
            return;
        }
        response.setBody(registerPage.get().mimeType(), registerPage.get().content());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<String> account = request.getParameter("account");
        Optional<String> password = request.getParameter("password");
        Optional<String> email = request.getParameter("email");
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            response.setError(HttpStatus.BAD_REQUEST);
            return;
        }

        User user = new User(account.get(), password.get(), email.get());
        InMemoryUserRepository.save(user);
        log.info("registered user : {}", user);
        response.setRedirect("/index.html");
    }
}
