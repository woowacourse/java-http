package nextstep.jwp.controller;

import static nextstep.jwp.http.common.HttpStatus.*;

import nextstep.jwp.controller.request.LoginRequest;
import nextstep.jwp.controller.response.LoginResponse;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.StaticResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends RestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;

    public LoginController(LoginService loginService, StaticResourceService staticResourceService) {
        super(staticResourceService);
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        try {
            if (httpRequest.hasCookie() && loginService.isAlreadyLogin(httpRequest.getCookie())) {
                return HttpResponse.redirect(FOUND, "/index.html");
            }

            LoginRequest loginRequest = getLoginRequest(httpRequest);
            LoginResponse loginResponse = loginService.login(loginRequest);

            LOGGER.debug("Login Success.");

            return HttpResponse.redirectWithSetCookie(FOUND, "/index.html", loginResponse.getSessionId());
        } catch (UnauthorizedException e) {
            LOGGER.debug("Login Failed.");

            return HttpResponse.redirect(UNAUTHORIZED, "/401.html");
        }
    }

    private LoginRequest getLoginRequest(HttpRequest httpRequest) {
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");

        LOGGER.debug("Login Request => account: {}", account);

        return new LoginRequest(account, password);
    }
}
