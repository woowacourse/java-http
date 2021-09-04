package nextstep.jwp.controller;

import static nextstep.jwp.controller.StaticResourcePath.INDEX_PAGE;
import static nextstep.jwp.controller.StaticResourcePath.NOT_FOUND_PAGE;
import static nextstep.jwp.controller.StaticResourcePath.UNAUTHORIZED_PAGE;
import static nextstep.jwp.http.common.HttpStatus.*;

import java.io.IOException;
import nextstep.jwp.controller.request.LoginRequest;
import nextstep.jwp.controller.response.LoginResponse;
import nextstep.jwp.exception.StaticResourceNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpStatus;
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
        this.loginService = loginService;
        this.staticResourceService = staticResourceService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        try {
            if (httpRequest.hasCookie() && loginService.isAlreadyLogin(httpRequest.getCookie())) {
                return HttpResponse.redirect(FOUND, INDEX_PAGE.getValue());
            }
            
            StaticResource staticResource = staticResourceService
                .findByPathWithExtension(httpRequest.getUri(), ".html");

            return HttpResponse.withBody(HttpStatus.OK, staticResource);
        } catch (StaticResourceNotFoundException e) {
            StaticResource staticResource = staticResourceService.findByPath(NOT_FOUND_PAGE.getValue());

            LOGGER.warn(e.getMessage());

            return HttpResponse.withBody(HttpStatus.NOT_FOUND, staticResource);
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        try {
            if (httpRequest.hasCookie() && loginService.isAlreadyLogin(httpRequest.getCookie())) {
                return HttpResponse.redirect(FOUND, INDEX_PAGE.getValue());
            }

            LoginRequest loginRequest = getLoginRequest(httpRequest);
            LoginResponse loginResponse = loginService.login(loginRequest);

            LOGGER.debug("Login Success.");

            StaticResource resource = staticResourceService.findByPath(INDEX_PAGE.getValue());
            return HttpResponse.withBodyAndCookie(OK, resource, loginResponse.toCookieString());
        } catch (UnauthorizedException e) {
            LOGGER.debug("Login Failed.");

            StaticResource resource = staticResourceService.findByPath(UNAUTHORIZED_PAGE.getValue());
            return HttpResponse.withBody(HttpStatus.UNAUTHORIZED, resource);
        }
    }

    private LoginRequest getLoginRequest(HttpRequest httpRequest) {
        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");

        LOGGER.debug("Login Request => account: {}", account);

        return new LoginRequest(account, password);
    }
}
