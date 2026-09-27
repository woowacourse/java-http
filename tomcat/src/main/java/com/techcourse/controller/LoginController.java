package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceRenderer;

public class LoginController extends AbstractController {

    private static final String LOGIN_PAGE = "/login.html";

    private final StaticResourceRenderer resourceRenderer;

    public LoginController(final StaticResourceRenderer resourceRenderer) {
        this.resourceRenderer = resourceRenderer;
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        if (request.session().getAttribute("user") instanceof User) {
            response.sendRedirect("/index.html");
            return;
        }

        resourceRenderer.writeResource(LOGIN_PAGE, response);
    }

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        String account = request.findFormParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
        String password = request.findFormParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));

        Optional<User> authenticatedUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (authenticatedUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        Session renewedSession = request.renewSession();
        renewedSession.setAttribute("user", authenticatedUser.get());

        response.sendRedirect("/index.html");
    }
}
