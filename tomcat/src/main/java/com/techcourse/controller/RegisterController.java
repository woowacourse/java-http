package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyAttribute("account");
        final String email = request.getBodyAttribute("email");
        final String password = request.getBodyAttribute("password");

        InMemoryUserRepository.save(new User(account, password, email));
        response.setRedirect("index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatusCode.OK);
        response.setResource(ResourcesReader.read(Path.from("register.html")));
    }
}
