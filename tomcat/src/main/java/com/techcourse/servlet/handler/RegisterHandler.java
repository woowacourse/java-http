package com.techcourse.servlet.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.servlet.Handler;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;

public class RegisterHandler implements Handler {
    private static final HttpMethod METHOD = HttpMethod.POST;
    private static final String PATH = "/register";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    @Override
    public boolean support(HttpRequest request) {
        return PATH.equals(request.getPath())
                && METHOD == request.getMethod()
                && request.matchHeader("Content-Type", CONTENT_TYPE);
    }

    @Override
    public View handle(HttpRequest request) {
        Map<String, String> formData = parsedFormData(request.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        return View.redirect("/index.html");
    }

    private Map<String, String> parsedFormData(String body) {
        Map<String, String> formData = new HashMap<>();
        for(String data : body.split("&")) {
            String[] dataComponent = data.split("=");
            formData.put(dataComponent[0], dataComponent[1]);
        }
        return formData;
    }
}
