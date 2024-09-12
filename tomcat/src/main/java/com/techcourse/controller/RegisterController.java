package com.techcourse.controller;

import java.util.Map;

import org.apache.catalina.mvc.AbstractController;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.HttpStatus;

import com.techcourse.service.AuthService;
import com.techcourse.service.RegisterService;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private final AuthService authService;
    private final RegisterService registerService;

    public RegisterController() {
        this.authService = new AuthService();
        this.registerService = new RegisterService();
    }

    @Override
    public boolean isMatchesRequest(HttpRequest request) {
        return REGISTER_PATH.equals(request.getPath());
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie().getAuthSessionId();
        if (authService.isLogin(sessionId)) {
            response.setHttpStatus(HttpStatus.FOUND);
            response.setBody(FileReader.loadFileContent(INDEX_PAGE));
            response.setRedirection(INDEX_PAGE);
            return;
        }
        response.setBody(FileReader.loadFileContent(REGISTER_PAGE));
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> bodyParams = request.getBody();
        if (hasMissingRequiredParams(bodyParams)) {
            throw new IllegalArgumentException("바디가 제대로 정의되지 않았습니다.");
        }
        registerAndSetResponse(response, bodyParams);
    }

    private boolean hasMissingRequiredParams(Map<String, String> queryParams) {
        return queryParams.size() < 3 ||
                queryParams.get(ACCOUNT) == null ||
                queryParams.get(PASSWORD) == null ||
                queryParams.get(EMAIL) == null;
    }

    private void registerAndSetResponse(HttpResponse response, Map<String, String> bodyParams) {
        String account = bodyParams.get(ACCOUNT);
        String password = bodyParams.get(PASSWORD);
        String email = bodyParams.get(EMAIL);
        registerService.registerUser(account, password, email);

        response.setHttpStatus(HttpStatus.FOUND);
        response.setBody(FileReader.loadFileContent(INDEX_PAGE));
        response.setRedirection(INDEX_PAGE);
    }
}
