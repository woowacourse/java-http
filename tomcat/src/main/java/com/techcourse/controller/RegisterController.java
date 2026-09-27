package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.StaticResource;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");
        final String email = request.getBodyParameter("email");
        InMemoryUserRepository.save(new User(account, password, email));
        response.redirect("/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.ok(ContentType.HTML, StaticResource.read("/register.html"));
    }
}
