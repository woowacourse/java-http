package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.routing.controller.Controller;
import org.apache.catalina.routing.requestMapping.RequestMapping;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

@Controller
public class RegisterController {

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public String getPage(final HttpRequest request, final HttpResponse response) {
        return "/register.html";
    }

    @RequestMapping(method = HttpMethod.POST, path = "/register")
    public String register(final HttpRequest request, final HttpResponse response) {
        saveUser(request);
        response.sendRedirect("/index.html");
        return "/index.html";
    }

    private void saveUser(final HttpRequest request) {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");
        final String email = request.getBodyParameter("email");
        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
