package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.service.RegisterService;

public class RegisterController implements Controller {

    private final RegisterService registerService = new RegisterService();

    @Override
    public void get(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.transfer(httpRequest.getUrl());
    }

    @Override
    public void post(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        registerService.registerUser(httpRequest, httpResponse);
    }
}
