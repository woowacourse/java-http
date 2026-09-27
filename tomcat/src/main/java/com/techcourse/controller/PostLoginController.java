package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.catalina.routing.Controller;
import org.apache.catalina.routing.RouteInfo;
import org.apache.catalina.routing.RouteKey;

public class PostLoginController implements Controller, RouteInfo {
    public static final String LOGIN_USER = "loginUser";

    public String handle(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");

        final Optional<User> loginUser = authenticate(account, password);
        if (loginUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return "로그인에 실패했습니다. 오류 안내 페이지로 이동합니다.";
        }

        final HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_USER, loginUser.get());
        response.sendRedirect("/index.html");
        return "로그인에 성공했습니다. 기본 페이지로 이동합니다.";
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
