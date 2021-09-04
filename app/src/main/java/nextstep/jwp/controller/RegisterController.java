package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.service.RegisterService;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = new RegisterService();

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        registerService.registerUser(httpRequest, httpResponse);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        registerService.doGet(httpResponse);
    }
}
