package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.handler.ViewHttpHandler;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.QueryParameters;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService service;
    private final ViewHttpHandler viewHttpHandler;

    public LoginController(UserService service) {
        this.service = service;
        this.viewHttpHandler = new ViewHttpHandler("login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        QueryParameters queryParameters = request.getQueryParameters();

        String account = queryParameters.getSingleValueByKey("account");
        String password = queryParameters.getSingleValueByKey("password");

        User user = service.findUserByAccountAndPassword(account, password);
        log.info("user: {}", user);

        return viewHttpHandler.handle(request);
    }
}
