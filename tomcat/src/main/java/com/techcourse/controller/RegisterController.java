package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceRenderer;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PAGE = "/register.html";

    private final StaticResourceRenderer resourceRenderer;

    public RegisterController(final StaticResourceRenderer resourceRenderer) {
        this.resourceRenderer = resourceRenderer;
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        resourceRenderer.writeResource(REGISTER_PAGE, response);
    }

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        String account = request.findFormParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
        String password = request.findFormParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));
        String email = request.findFormParameter("email")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: email"));

        InMemoryUserRepository.save(new User(account, password, email));

        response.sendRedirect("/index.html");
    }
}
