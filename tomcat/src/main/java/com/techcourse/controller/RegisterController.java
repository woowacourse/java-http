package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController implements Controller {

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> formData = request.body().parseFormData();
        String account = formData.get("account");
        String email = formData.get("email");
        String password = formData.get("password");

        InMemoryUserRepository.save(new User(account, password, email));

        return HttpResponse.redirect("/index.html");
    }
}
