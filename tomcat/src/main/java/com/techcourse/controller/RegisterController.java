package com.techcourse.controller;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.controller.StaticResourceController;

public class RegisterController extends AbstractController {

    private final StaticResourceController staticResource;

    public RegisterController(StaticResourceController staticResource) {
        this.staticResource = staticResource;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        staticResource.serve("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (isAnyBlank(account, password, email)) {
            response.sendError(HttpStatus.BAD_REQUEST);
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }
}
