package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.resource.StaticResourceService;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public final class RegisterController extends AbstractController {

    private final StaticResourceService resources = new StaticResourceService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        resources.serve("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            response.sendError(HttpStatus.BAD_REQUEST, "Required fields must not be blank");
            return;
        }

        boolean registered = InMemoryUserRepository.save(new User(account, password, email));

        if (!registered) {
            response.sendError(HttpStatus.CONFLICT, "Account already exists");
            return;
        }
        response.sendRedirect("/index.html");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
