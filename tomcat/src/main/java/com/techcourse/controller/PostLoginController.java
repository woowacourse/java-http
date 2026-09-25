package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.routing.Controller;
import org.apache.coyote.routing.RouteInfo;
import org.apache.coyote.routing.RouteKey;

public class PostLoginController implements Controller, RouteInfo {
    public static final String LOGIN_USER = "loginUser";

    public String handle(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");

        final Optional<User> loginUser = authenticate(account, password);
        if (loginUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return "/401.html";
        }

        final HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_USER, loginUser.get());
        response.sendRedirect("/index.html");
        return "/index.html";
    }

    @Override
    public RouteKey getRouteKey() {
        return new RouteKey(HttpMethod.POST, "/login");
    }

    private Optional<User> authenticate(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
