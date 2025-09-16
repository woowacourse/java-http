package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpCookie cookie = request.getCookie();
        final String jSessionId = cookie.getJSessionId();
        final Session session = sessionManager.findSession(jSessionId);

        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        response.setBody(new String(request.readAllBytes("/login.html")));
        response.send();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final var user = InMemoryUserRepository.findByAccount(request.getParameter("account"));
        if (user.isPresent() && user.get().checkPassword(request.getParameter("password"))) {
            final String jSessionId = UUID.randomUUID().toString();
            final Session session = new Session(jSessionId);
            session.setAttribute("user", user.get());
            sessionManager.add(session);
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/401.html");
    }
}

