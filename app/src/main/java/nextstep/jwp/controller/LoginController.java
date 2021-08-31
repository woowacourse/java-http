package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.service.LoginService;

public class LoginController implements Controller {

    private final LoginService loginService = new LoginService();

    @Override
    public void get(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.transfer(httpRequest.getUrl());
    }

    @Override
    public void post(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        loginService.login(httpRequest, httpResponse);
    }
}
