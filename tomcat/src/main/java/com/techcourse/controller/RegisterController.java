package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<Session> session = request.getSession();
        if (session.isPresent()) {
            response.sendRedirect("/index.html");
            return;
        }
        response.sendStaticResource("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        User user = createUser(request);
        Session session = createUserSession(user);

        response.sendRedirect("/index.html");
        response.setSession(session);
    }

    private User createUser(HttpRequest request) {
        Optional<String> password = request.getParameter("password");
        Optional<String> email = request.getParameter("email");
        Optional<String> account = request.getParameter("account");
        if (password.isEmpty() || email.isEmpty() || account.isEmpty()) {
            throw new UncheckedServletException("요청이 잘못되었습니다.");
        }
        return new User(account.get(), password.get(), email.get());
    }

    private Session createUserSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        return session;
    }
}
