package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PAGE = "/register.html";

    private final StaticResourceResolver staticResourceResolver = new StaticResourceResolver();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        sendRegisterPage(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account").orElse(null);
        String password = request.getParameter("password").orElse(null);
        String email = request.getParameter("email").orElse(null);

        try {
            InMemoryUserRepository.save(new User(account, password, email));
        } catch (IllegalArgumentException e) {
            response.redirect("/401.html");
            return;
        }

        response.redirect("/index.html");
    }

    private void sendRegisterPage(HttpResponse response) {
        staticResourceResolver.read(REGISTER_PAGE)
                .ifPresentOrElse(
                        body -> response.send(HttpStatus.OK, REGISTER_PAGE, body),
                        () -> response.sendError(HttpStatus.NOT_FOUND)
                );
    }
}
