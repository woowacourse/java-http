package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import org.apache.controller.AbstractController;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.util.QueryStringParser;

public class RegisterController extends AbstractController {

    public RegisterController() {
        super("/register");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody();
        Map<String, List<String>> queryStrings = QueryStringParser.parseQueryString(body);
        String account = queryStrings.get("account").getFirst();
        String email = queryStrings.get("email").getFirst();
        String password = queryStrings.get("password").getFirst();

        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.addStaticBody("/register.html");
    }
}
