package com.techcourse.controller;

import com.techcourse.ResourceLoader;
import com.techcourse.model.User;
import com.techcourse.service.AuthService;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final AuthService authService = new AuthService();

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        String path = request.getPath();
        return Optional.ofNullable(request.getHeaders().get("Cookie"))
                .flatMap(HttpCookie::getSessionId)
                .map(SessionManager.getInstance()::findSession)
                .filter(session -> session.getAttribute("user") != null)
                .map(session -> HttpResponse.redirect("/index.html"))
                .orElse(HttpResponse.ok(ResourceLoader.readWithFallback(path), ResourceLoader.getMimeType(path)));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        try {
            User user = authService.login(account, password);
            return createAuthenticatedResponse(user);
        } catch (IllegalArgumentException e) {
            return HttpResponse.redirect("/401.html");
        }
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
