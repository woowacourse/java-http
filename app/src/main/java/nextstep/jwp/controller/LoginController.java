package nextstep.jwp.controller;

import static nextstep.jwp.http.common.HttpStatus.*;

import java.io.IOException;
import nextstep.jwp.controller.request.LoginRequest;
import nextstep.jwp.controller.response.LoginResponse;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.StaticResource;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.StaticResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends RestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;
    private final StaticResourceService staticResourceService;

    public LoginController(LoginService loginService, StaticResourceService staticResourceService) {
        super(staticResourceService);
        this.loginService = loginService;
        this.staticResourceService = staticResourceService;
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        try {
            if (httpRequest.hasCookie() && loginService.isAlreadyLogin(httpRequest.getCookie())) {
                return HttpResponse.redirect(FOUND, "/index.html");
            }

            LoginRequest loginRequest = getLoginRequest(httpRequest);
            LoginResponse loginResponse = loginService.login(loginRequest);

            LOGGER.debug("Login Success.");

            StaticResource resource = staticResourceService.findByPath("/index.html");
            return HttpResponse.withBodyAndCookie(OK, resource, loginResponse.toCookieString());
        } catch (UnauthorizedException e) {
            LOGGER.debug("Login Failed.");

            StaticResource resource = staticResourceService.findByPath("/401.html");
            return HttpResponse.withBody(UNAUTHORIZED, resource);
        }
    }

    private LoginRequest getLoginRequest(HttpRequest httpRequest) {
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");

        LOGGER.debug("Login Request => account: {}", account);

        return new LoginRequest(account, password);
    }
}
