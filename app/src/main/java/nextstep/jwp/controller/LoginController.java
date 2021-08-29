package nextstep.jwp.controller;

import java.io.IOException;
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

    private HttpResponse doGetWithQueryParam(HttpRequest httpRequest) {
        String account = httpRequest.getUriParameter("account");
        String password = httpRequest.getUriParameter("password");

        if (loginService.login(account, password)) {
            return HttpResponse.redirect(HttpStatus.FOUND, "/index.html");
        }
        return HttpResponse.redirect(HttpStatus.FOUND, "/401.html");
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        if (httpRequest.hasQueryParam()) {
            return doGetWithQueryParam(httpRequest);
        }

        return doGet(httpRequest);
    }

    @Override
    public boolean matchUri(String uri) {
        return uri.startsWith("/login");
    }
}
