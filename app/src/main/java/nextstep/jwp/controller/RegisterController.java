package nextstep.jwp.controller;

import nextstep.jwp.controller.request.RegisterRequest;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.StaticResourceService;

public class RegisterController extends RestController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService, StaticResourceService staticResourceService) {
        super(staticResourceService);
        this.registerService = registerService;
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        RegisterRequest registerRequest = getRegisterRequest(httpRequest);
        registerService.registerUser(registerRequest);

        return HttpResponse.redirect(HttpStatus.MOVED_PERMANENTLY, "/index.html");
    }

    private RegisterRequest getRegisterRequest(HttpRequest httpRequest) {
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");
        String email = httpRequest.getBodyParameter("email");

        return new RegisterRequest(account, password, email);
    }

    @Override
    public boolean matchUri(String uri) {
        return uri.startsWith("/register");
    }
}
