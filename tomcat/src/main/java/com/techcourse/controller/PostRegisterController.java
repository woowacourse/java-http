package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.routing.Controller;
import org.apache.coyote.routing.RouteInfo;
import org.apache.coyote.routing.RouteKey;

public class PostRegisterController implements Controller, RouteInfo {

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        saveUser(request);
        response.sendRedirect("/index.html");
        return "/index.html";
    }

    @Override
    public RouteKey getRouteKey() {
        return new RouteKey(HttpMethod.POST, "/register");
    }

    private void saveUser(final HttpRequest request) {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");
        final String email = request.getBodyParameter("email");
        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
