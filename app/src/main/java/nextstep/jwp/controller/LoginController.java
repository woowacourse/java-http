package nextstep.jwp.controller;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.StaticResourceService;

public class LoginController extends RestController {

    private final LoginService loginService;

    public LoginController(LoginService loginService, StaticResourceService staticResourceService) {
        super(staticResourceService);
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");

        try {
            loginService.login(account, password);
            return HttpResponse.redirect(HttpStatus.FOUND, "/index.html");
        } catch (UnauthorizedException e) {
            return HttpResponse.redirect(HttpStatus.FOUND, "/401.html");
        }
    }

    @Override
    public boolean matchUri(String uri) {
        return uri.startsWith("/login");
    }
}
