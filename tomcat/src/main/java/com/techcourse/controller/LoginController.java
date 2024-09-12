package com.techcourse.controller;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;
import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;
import org.apache.catalina.response.StatusLine;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {
    public static final String LOGIN_PATH = "/login";
    private static final String BAD_REQUEST_PAGE = "/400.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public boolean matchesRequest(HttpRequest request) {
        return LOGIN_PATH.equals(request.getPathWithoutQuery());
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        if(LOGIN_PATH.equals(request.getPath())) {
            return new HttpResponse(
                    new StatusLine(request.getVersionOfProtocol(), HttpStatus.OK),
                    request.getFileType(),
                    FileReader.loadFileContent(request.getPath() + ".html"));
        }

        Map<String, String> queryParams = request.getQueryParam();
        if (hasMissingRequiredParams(queryParams)) {
            return getBadRequestResponse(request);
        }
        Optional<User> authenticatedUser = getAuthenticatedUser(queryParams.get(ACCOUNT), queryParams.get(PASSWORD));
        return authenticatedUser.map(user -> getLoginSuccessResponse(request, user))
                .orElseGet(() -> getUnauthorizedResponse(request));
    }

    private boolean hasMissingRequiredParams(Map<String, String> queryParams) {
        return queryParams.size() < 2 ||
                queryParams.get(ACCOUNT) == null || queryParams.get(PASSWORD) == null;
    }

    private static HttpResponse getBadRequestResponse(HttpRequest request) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.BAD_REQUEST),
                request.getFileType(),
                FileReader.loadFileContent(BAD_REQUEST_PAGE));
    }

    private Optional<User> getAuthenticatedUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("인증된 사용자: {}", user.get());
            return user;
        }
        return Optional.empty();
    }

    private static HttpResponse getLoginSuccessResponse(HttpRequest request, User user) {
        Session session = createSession(user);
        SessionManager.getInstance().add(session);
        HttpCookie httpCookie = request.getCookie();
        String cookie = httpCookie.getCookies(session.getId());

        HttpResponse response = new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.FOUND),
                request.getFileType(),
                FileReader.loadFileContent(INDEX_PAGE));
        response.setCookie(cookie);
        response.addLocation(INDEX_PAGE);
        return response;
    }

    private static Session createSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(session.getId(), user);
        return session;
    }

    private static HttpResponse getUnauthorizedResponse(HttpRequest request) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.UNAUTHORIZED),
                request.getFileType(),
                FileReader.loadFileContent(UNAUTHORIZED_PAGE));
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        return null;
    }
}
