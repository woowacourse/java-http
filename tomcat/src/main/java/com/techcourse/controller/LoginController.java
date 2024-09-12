package com.techcourse.controller;

import com.techcourse.exception.TechcourseException;
import com.techcourse.model.User;
import com.techcourse.model.UserService;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpRequestBody body = request.getBody();
        final UserService userService = UserService.getInstance();
        try {
            final User user = userService.login(body.get("account"), body.get("password"));
            final Session session = request.createSession();
            session.setAttribute("user", user);
            postLoginSuccess(response, session);
        } catch (TechcourseException e) {
            postLoginFailed(response);
        }
    }

    private void postLoginSuccess(final HttpResponse response, final Session session) {
        final String location = "/index.html";
        final HttpCookie cookie = HttpCookie.ofJSessionId(session.getId());
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation(location);
        response.setCookie(cookie);
    }

    private void postLoginFailed(final HttpResponse response) {
        final String location = "/401.html";
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation(location);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (checkLogin(request)) {
            getLoginPageWithLogin(response);
            return;
        }
        getLoginPageWithoutLogin(response);
    }

    private boolean checkLogin(final HttpRequest request) {
        Session session = request.getSession();
        return session != null;
    }

    private void getLoginPageWithLogin(final HttpResponse response) {
        final String path = "/index.html";
        final String body = viewResolver.resolve(path);
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(path, body);
    }

    private void getLoginPageWithoutLogin(final HttpResponse response) {
        final String path = "/login.html";
        final String body = viewResolver.resolve(path);
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(path, body);
    }
}
