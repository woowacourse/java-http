package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.AuthService;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final AuthService authService = new AuthService();

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        User user = authService.register(account, password, email);

        return createAuthenticatedResponse(user);
    }

    private HttpResponse createAuthenticatedResponse(final User user) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        
        String setCookieHeader = HttpCookie.createJSessionIdSetCookieHeader(sessionId);
        HttpResponse response = HttpResponse.redirect("/index.html");
        response.addHeader("Set-Cookie", setCookieHeader);
        return response;
    }
}
