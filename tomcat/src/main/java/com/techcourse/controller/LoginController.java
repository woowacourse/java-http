package com.techcourse.controller;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.auth.Session;
import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.StatusLine;

import com.techcourse.model.User;
import com.techcourse.service.AuthService;
import com.techcourse.service.LoginService;

public class LoginController extends AbstractController {

    public static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private final LoginService loginService;
    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
        this.loginService = new LoginService(authService);
    }

    @Override
    public boolean isMatchesRequest(HttpRequest request) {
        return LOGIN_PATH.equals(request.getPathWithoutQuery());
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        if (authService.isLogin(request.getCookie())) {
            return createRedirectResponse(request, HttpStatus.FOUND, INDEX_PAGE);
        }

        if (request.isEmptyByQueryParam()) {
            return createFileResponse(request, LOGIN_PAGE);
        }

        Map<String, String> queryParams = request.getQueryParam();
        if (hasMissingRequiredParams(queryParams)) {
            throw new IllegalArgumentException("파라미터가 제대로 정의되지 않았습니다.");
        }

        Optional<User> authenticatedUser = loginService.login(queryParams.get(ACCOUNT), queryParams.get(PASSWORD));
        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            Session session = authService.createSession(user);
            HttpCookie httpCookie = new HttpCookie();
            httpCookie.addAuthSessionId(session.getId());

            HttpResponse response = createRedirectResponse(request, HttpStatus.FOUND, INDEX_PAGE);
            response.setCookie(httpCookie.toString());
            return response;
        } else {
            return createFileResponse(request, HttpStatus.UNAUTHORIZED, UNAUTHORIZED_PAGE);
        }
    }

    private boolean hasMissingRequiredParams(Map<String, String> queryParams) {
        return queryParams.size() < 2 ||
                queryParams.get(ACCOUNT) == null || queryParams.get(PASSWORD) == null;
    }

    private HttpResponse createRedirectResponse(HttpRequest request, HttpStatus status, String path) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), status),
                request.getContentType(),
                FileReader.loadFileContent(path)
        ).addLocation(path);
    }

    private HttpResponse createFileResponse(HttpRequest request, String path) {
        return createFileResponse(request, HttpStatus.OK, path);
    }

    private HttpResponse createFileResponse(HttpRequest request, HttpStatus status, String path) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), status),
                request.getContentType(),
                FileReader.loadFileContent(path)
        );
    }
}
