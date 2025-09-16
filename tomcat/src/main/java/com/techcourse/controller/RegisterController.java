package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class RegisterController extends AbstractController {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setBody(new String(request.readAllBytes("/register.html")));
        response.send();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final var user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(user);

        final String jSessionId = UUID.randomUUID().toString();
        final Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        response.sendRedirect("/index.html");
    }
}
