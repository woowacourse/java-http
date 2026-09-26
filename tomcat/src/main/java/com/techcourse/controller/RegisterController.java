package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.MappedController;
import org.apache.catalina.StaticResourceResponder;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController implements MappedController {

    private static final long DEFAULT_USER_ID = 999L;

    private final StaticResourceResponder resourceResponder = new StaticResourceResponder();

    @Override
    public String getPath() {
        return "/register";
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return resourceResponder.serve("/register.html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getBody().get("account");
        String password = request.getBody().get("password");
        String email = request.getBody().get("email");
        User user = new User(DEFAULT_USER_ID, account, password, email);
        InMemoryUserRepository.save(user);

        HttpResponse response = new HttpResponse();
        response.redirect("/index.html");
        return response;
    }
}
