package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private final Controller staticResourceController = new StaticResourceController();

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return staticResourceController.handle(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        Map<String, String> formData = request.body().parseFormData();
        String account = formData.get("account");
        String email = formData.get("email");
        String password = formData.get("password");

        InMemoryUserRepository.save(new User(account, password, email));

        return HttpResponse.redirect("/index.html");
    }
}
