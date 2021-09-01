package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.GeneralResponse;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.LoginResponse;
import nextstep.jwp.service.LoginService;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (loginService.isSessionLogin(request)) {
            final LoginResponse loginResponse = new LoginResponse();
            final String token = loginService.token(request);
            response.setResponse(loginResponse.successResponse(token));
            return;
        }
        final GeneralResponse generalResponse = new GeneralResponse(request);
        response.setResponse(generalResponse.getResponse());
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final boolean isSuccess = loginService.login(request);
        final String loginResult = loginResult(request, isSuccess);
        response.setResponse(loginResult);
    }

    private String loginResult(HttpRequest request, boolean isSuccess) throws Exception {
        final LoginResponse loginResponse = new LoginResponse();
        final String jSESSIONID = loginService.token(request);
        if (isSuccess) {
            return loginResponse.successResponse(jSESSIONID);
        }
        return loginResponse.failedResponse();
    }

}
