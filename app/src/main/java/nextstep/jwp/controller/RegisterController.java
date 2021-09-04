package nextstep.jwp.controller;

import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;
import nextstep.jwp.service.RegisterService;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController() {
        this.registerService = new RegisterService();
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.ok(httpRequest.getRequestUrl() + ".html");
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        registerService.register(httpRequest);
        httpResponse.redirect("/index.html");
    }
}
