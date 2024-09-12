package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String LOGIN_REQUEST_URI = "/login";
    private static final String DEFAULT_REQUEST_URI = "/index";
    private static final String UNAUTHORIZED_REQUEST_URI = "/401";

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            if (request.getSession(false) != null) {
                response.sendRedirect(DEFAULT_REQUEST_URI);
                return;
            }
            Map<String, String> queryParams = request.getQueryParams();
            if (!queryParams.isEmpty()) {
                userService.findUser(queryParams);
            }
            response.addStaticResource(LOGIN_REQUEST_URI);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(UNAUTHORIZED_REQUEST_URI);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            RequestBody body = request.getBody();
            userService.findUser(body.getParameters());
            Session session = request.getSession(true);
            SessionManager.getInstance().add(session);
            response.sendRedirect(DEFAULT_REQUEST_URI);
            response.addCookie(HttpCookie.ofJSessionId(session.getId()));
        } catch (IllegalArgumentException e) {
            response.sendRedirect(UNAUTHORIZED_REQUEST_URI);
        }
    }
}
