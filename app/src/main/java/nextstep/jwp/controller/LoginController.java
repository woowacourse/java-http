package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.response.GeneralResponse;
import nextstep.jwp.http.response.LoginResponse;
import nextstep.jwp.service.LoginService;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final boolean isSuccess = loginService.login(request);
        final LoginResponse loginResponse = new LoginResponse();
        final String loginResult = loginResult(isSuccess, loginResponse);
        response.setResponse(loginResult);
    }

    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final GeneralResponse generalResponse = new GeneralResponse(request);
        response.setResponse(generalResponse.getResponse());
    }

    private String loginResult(boolean isSuccess, LoginResponse loginResponse) throws IOException {
        if (isSuccess) {
            return loginResponse.successResponse();
        }
        return loginResponse.failedResponse();
    }

}
