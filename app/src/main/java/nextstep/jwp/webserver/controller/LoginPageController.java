package nextstep.jwp.webserver.controller;

import java.util.EnumSet;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.*;
import nextstep.jwp.webserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPageController.class);

    private final UserService userService;

    public LoginPageController() {
        super("/login", EnumSet.of(HttpMethod.GET));
        this.userService = new UserService();
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.getQueries().isEmpty()) {
            return new ResourceResponseTemplate().ok("/login.html");
        }

        final String account = httpRequest.getValueFromQuery("account");
        final String password = httpRequest.getValueFromQuery("password");
        LOGGER.debug("로그인 요청 - [account : {}, password : {}]", account, password);

        if (!userService.login(account, password)) {
            return new ResourceResponseTemplate().unauthorized("/401.html");
        }

        return new StringResponseTemplate().found("/index.html");
    }
}
