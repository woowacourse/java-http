package com.techcourse.web.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.cookie.HttpCookie;
import com.techcourse.web.resource.StaticResourceHandler;
import org.apache.catalina.connector.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.IOException;
import java.util.Map;

public class LoginController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler;

    public LoginController(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (isLogin(request.getHeader("Cookie"))) {
            response.redirectTo("/index.html");
            return;
        }

        staticResourceHandler.serve(request.getPathUri(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        User user = login(request);

        if (user != null) {
            String jsessionId = storeSession(request.getHeader("Cookie"), user);
            response.addCookie("JSESSIONID", jsessionId)
                    .redirectTo("/index.html");
            return;
        }

        response.status(HttpStatus.UNAUTHORIZED);
        staticResourceHandler.serve("/401.html", response);
    }

    private User login(HttpRequest request) {
        Map<String, String> formParams = request.getFormParams();

        if (formParams.get("account") == null || formParams.get("password") == null) {
            return null;
        }

        return InMemoryUserRepository.findByAccount(formParams.get("account"))
                .filter(user -> user.checkPassword(formParams.get("password")))
                .orElse(null);
    }

    private String storeSession(String cookie, User user) {
        String jsessionId = HttpCookie.getJsessionId(cookie);
        SessionManager sessionManager = SessionManager.getInstance();

        Session oldSession = sessionManager.findSession(jsessionId);
        if (oldSession != null) {
            oldSession.invalidate();
            sessionManager.remove(oldSession.getJsessionId());
        }

        Session newSession = new Session();
        sessionManager.add(newSession);
        newSession.setAttribute("user", user);

        return newSession.getJsessionId();
    }

    private boolean isLogin(String cookie) {
        String jsessionId = HttpCookie.getJsessionId(cookie);

        if (jsessionId == null || jsessionId.isBlank()) {
            return false;
        }

        Session session = SessionManager.getInstance().findSession(jsessionId);
        return session != null && session.getAttribute("user") != null;
    }
}
