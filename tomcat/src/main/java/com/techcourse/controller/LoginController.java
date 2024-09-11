package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.net.URI;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final URI REDIRECT_URI = URI.create("/index.html");
    private static final URI UNAUTHORIZED_URI = URI.create("/401.html");

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            if (request.getSession(false) != null) {
                response.sendRedirect(REDIRECT_URI.getPath());
                return;
            }
            Map<String, String> queryParams = request.getQueryParams();
            if (!queryParams.isEmpty()) {
                userService.findUser(queryParams);
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect(UNAUTHORIZED_URI.getPath());
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            RequestBody body = request.getBody();
            userService.findUser(body.getParameters());
            Session session = request.getSession(true);
            SessionManager.getInstance().add(session);
            response.sendRedirect(REDIRECT_URI.getPath());
            response.addCookie(HttpCookie.ofJSessionId(session.getId()));
        } catch (IllegalArgumentException e) {
            response.sendRedirect(UNAUTHORIZED_URI.getPath());
        }
    }
}
