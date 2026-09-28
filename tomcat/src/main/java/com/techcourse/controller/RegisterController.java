package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.resource.Resource;
import org.apache.catalina.resource.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        showRegisterPage(response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");
        if (register(account, password, email)) {
            response.sendRedirect("/index.html");
            return;
        }
        showRegisterPage(response);
    }

    private boolean register(final String account, final String password, final String email) {
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            return false;
        }
        return InMemoryUserRepository.save(new User(account, password, email));
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private void showRegisterPage(final HttpResponse response) throws IOException {
        final Resource resource = ResourceReader.read("/register.html").orElseThrow();
        response.setBody(resource.contentType(), resource.content());
    }
}
