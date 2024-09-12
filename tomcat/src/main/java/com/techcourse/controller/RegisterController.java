package com.techcourse.controller;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;
import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public boolean isMatchesRequest(HttpRequest request) {
        return REGISTER_PATH.equals(request.getPath());
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        String id = request.getCookie().getAuthSessionId();
        Optional<Session> session = SessionManager.getInstance().findSession(id);
        if (session.isPresent()) {
            return getLoginSuccessResponse(request);
        }
        return HttpResponse.createFileOkResponse(request, REGISTER_PAGE);
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        return handleRegistration(request);
    }

    private HttpResponse handleRegistration(HttpRequest request) {
        Map<String, String> bodyParams = request.getBody();
        String account = bodyParams.get(ACCOUNT);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        String password = bodyParams.get(PASSWORD);
        String email = bodyParams.get(EMAIL);
        InMemoryUserRepository.save(new User(account, password, email));

        return getLoginSuccessResponse(request);
    }

    private static HttpResponse getLoginSuccessResponse(HttpRequest request) {
        return HttpResponse.createRedirectResponse(request, HttpStatus.FOUND, INDEX_PAGE);
    }
}
