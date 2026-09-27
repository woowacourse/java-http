package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class RegisterController extends AbstractController {

    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setBody(ContentType.HTML, StaticResourceLoader.load(REGISTER_PAGE));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if (account == null || account.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            response.sendRedirect(REGISTER_PAGE);
            return;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect(INDEX_PAGE);
    }
}
