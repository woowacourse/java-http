package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import com.techcourse.model.User;
import com.techcourse.param.LoginParam;
import com.techcourse.service.UserService;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;

import java.util.Optional;

public class LoginController extends AbstractController {

    private static final String LOGIN_PATH = "/login";
    public static final String MAIN_PATH = "/index.html";
    public static final String USER = "user";

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isSamePath(LOGIN_PATH);
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            LoginParam loginParam = new LoginParam(httpRequest);
            User user = userService.findUser(loginParam.getAccount(), loginParam.getPassword());

            setCookie(httpRequest, httpResponse, user);
            httpResponse.sendRedirect(MAIN_PATH);
        } catch (IllegalArgumentException e) {
            httpResponse.sendError(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            httpResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.hasCookie()) {
            isLoggedInUser(httpRequest, httpResponse);
        }
        httpResponse.sendStaticResourceResponse(httpRequest, HttpStatus.OK);
    }

    private void isLoggedInUser(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession(false);
        Optional<Object> user = session.findAttribute(USER);

        if (user.isPresent()) {
            httpResponse.sendRedirect(MAIN_PATH);
        }
    }

    private void setCookie(HttpRequest httpRequest, HttpResponse httpResponse, User user) {
        Session session = httpRequest.getSession(true);
        session.setAttribute(USER, user);

        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);

        httpResponse.setCookies(session.getId());
    }
}
