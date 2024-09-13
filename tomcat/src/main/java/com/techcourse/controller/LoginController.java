package com.techcourse.controller;

import java.util.Map;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.auth.Session;
import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;

import com.techcourse.model.User;
import com.techcourse.service.AuthService;
import com.techcourse.service.LoginService;

public class LoginController extends AbstractController {

    public static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private final LoginService loginService;
    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
        this.loginService = new LoginService();
    }

    @Override
    public boolean isMatchesRequest(HttpRequest request) {
        return (request.isSameHttpMethod(HttpMethod.GET) && LOGIN_PATH.equals(request.getPath())) ||
                (request.isSameHttpMethod(HttpMethod.POST) && LOGIN_PATH.equals(request.getPathWithoutQuery()));
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (authService.isLogin(request.getCookie().getAuthSessionId())) {
            response.setHttpStatus(HttpStatus.FOUND);
            response.setBody(FileReader.loadFileContent(INDEX_PAGE));
            response.setRedirection(INDEX_PAGE);
            return;
        }
        response.setBody(FileReader.loadFileContent(LOGIN_PAGE));
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> queryParams = request.getQueryParam();
        if (hasMissingRequiredParams(queryParams)) {
            throw new IllegalArgumentException("파라미터가 제대로 정의되지 않았습니다.");
        }
        User user = loginService.login(queryParams.get(ACCOUNT), queryParams.get(PASSWORD));
        loginAndSetResponse(response, user);
    }

    private boolean hasMissingRequiredParams(Map<String, String> queryParams) {
        return queryParams.size() != 2 ||
                queryParams.get(ACCOUNT) == null || queryParams.get(PASSWORD) == null;
    }

    private void loginAndSetResponse(HttpResponse response, User user) {
        Session session = authService.createSession(user);
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.addAuthSessionId(session.getId());

        response.setHttpStatus(HttpStatus.FOUND);
        response.setBody(FileReader.loadFileContent(INDEX_PAGE));
        response.setRedirection(INDEX_PAGE);
        response.setCookie(httpCookie.toString());
    }
}
