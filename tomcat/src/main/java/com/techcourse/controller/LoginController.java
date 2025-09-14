package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.SessionService;
import com.techcourse.service.UserService;
import org.apache.catalina.web.controller.AbstractController;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    public static final String ENDPOINT = "/login";

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String REDIRECTION_PATH = "/index.html";

    public final UserService userService;
    public final SessionService sessionService;

    public LoginController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::doGet);
        this.addCommand(HttpMethod.POST, this::doPost);
    }

    public String doGet(final Http11Request request, final Http11Response response) {
        if (sessionService.isSessionValid(request)) {
            return handleLoginSuccess(response);
        }
        return ENDPOINT;
    }

    public String doPost(final Http11Request request, final Http11Response response) {
        final String account = request.body().getValueByKey("account");
        final String password = request.body().getValueByKey("password");

        final User user = userService.findUser(account, password, response);
        log.info("User authenticated: {}", user);
        sessionService.createSession(user, response);
        return handleLoginSuccess(response);
    }

    private String handleLoginSuccess(final Http11Response response) {
        response.setState(HttpStatus.Found);
        return REDIRECTION_PATH;
    }
}
