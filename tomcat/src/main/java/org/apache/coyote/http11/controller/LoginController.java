package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.service.LoginService;

public class LoginController implements Controller {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Response<String> handle(RequestLine requestLine) {
        if (checkLogin(requestLine)) {
            return Response.status(302)
                .addHeader("Location", "/index.html")
                .build();
        }
        return Response.status(302)
            .addHeader("Location", "/401.html")
            .build();
    }

    private boolean checkLogin(RequestLine requestLine) {
        Map<String, String> queryParam = requestLine.getQueryParam();
        String account = queryParam.get(ACCOUNT);
        String password = queryParam.get(PASSWORD);
        if (account != null && password != null) {
            return loginService.checkUser(account, password);
        }
        return false;
    }
}
