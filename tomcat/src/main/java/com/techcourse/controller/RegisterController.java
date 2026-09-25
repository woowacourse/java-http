package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.resource.Resource;
import org.apache.catalina.resource.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Resource resource = ResourceReader.read("/register.html").orElseThrow();
        response.setBody(resource.contentType(), resource.content());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            final Resource resource = ResourceReader.read("/register.html").orElseThrow();
            response.setBody(resource.contentType(), resource.content());
            return;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}
