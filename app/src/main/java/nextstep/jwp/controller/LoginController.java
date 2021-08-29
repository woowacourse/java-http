package nextstep.jwp.controller;

import static nextstep.jwp.http.request.Method.GET;

import java.io.IOException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.StaticResourceService;

public class LoginController implements Controller {

    private final LoginService loginService;
    private final StaticResourceService staticResourceService;

    public LoginController(LoginService loginService, StaticResourceService staticResourceService) {
        this.loginService = loginService;
        this.staticResourceService = staticResourceService;
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        StaticResource staticResource = staticResourceService.findByPathWithExtension(
            httpRequest.getUri(), ".html");

        return HttpResponse.withBody(HttpStatus.OK, staticResource);
    }

    private HttpResponse doPost(HttpRequest httpRequest) {
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
    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        if (httpRequest.hasMethod(GET)) {
            return doGet(httpRequest);
        }

        return doPost(httpRequest);
    }

    @Override
    public boolean matchUri(String uri) {
        return uri.startsWith("/login");
    }
}
