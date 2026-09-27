package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.StaticResource;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        StaticResource.serve(REGISTER_PAGE, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final User registerUser = new User(
                request.getFormParameter("account"),
                request.getFormParameter("password"),
                request.getFormParameter("email"));
        InMemoryUserRepository.save(registerUser);
        response.sendRedirect(INDEX_PAGE);
    }
}
