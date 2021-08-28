package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.controller.request.RegisterRequest;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.Method;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.StaticResourceService;

public class RegisterController implements Controller {

    private final RegisterService registerService;
    private final StaticResourceService staticResourceService;

    public RegisterController(RegisterService registerService, StaticResourceService staticResourceService) {
        this.registerService = registerService;
        this.staticResourceService = staticResourceService;
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        StaticResource staticResource = staticResourceService.findByPathWithExtension(
            httpRequest.getUri(), ".html");

        return HttpResponse.withBody(HttpStatus.OK, staticResource);
    }

    private HttpResponse doPost(HttpRequest httpRequest) {
        RegisterRequest registerRequest = getRegisterRequest(httpRequest);
        registerService.registerUser(registerRequest);

        return HttpResponse.redirect(HttpStatus.PERMANENT_REDIRECT, "/index.html");
    }

    private RegisterRequest getRegisterRequest(HttpRequest httpRequest) {
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");
        String email = httpRequest.getBodyParameter("email");

        return new RegisterRequest(account, password, email);
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        if (httpRequest.hasMethod(Method.GET)) {
            return doGet(httpRequest);
        }

        return doPost(httpRequest);
    }

    @Override
    public boolean matchUri(String uri) {
        return uri.startsWith("/register");
    }
}
