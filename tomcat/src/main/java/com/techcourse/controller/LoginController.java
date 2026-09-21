package com.techcourse.controller;

import com.techcourse.service.ApplicationService;
import com.techcourse.web.StaticResourceHandler;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final ApplicationService applicationService;
    private final StaticResourceHandler staticResourceHandler;

    public LoginController(ApplicationService applicationService, StaticResourceHandler staticResourceHandler) {
        this.applicationService = applicationService;
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        login(request, response, request.queryParameters());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        login(request, response, request.formParameters());
    }

    private void login(HttpRequest request, HttpResponse response, Map<String, String> params) throws IOException {
        var session = request.session();
        if (session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        String account = params.get("account");
        String password = params.get("password");
        if (account != null && password != null) {
            var user = applicationService.login(account, password);
            if (user.isPresent()) {
                log.info("Login successful: account={}", user.get().getAccount());
                session.setAttribute("user", user.get());
                response.sendRedirect("/index.html");
                return;
            }
            response.sendRedirect("/401.html");
            return;
        }

        response.copyFrom(staticResourceHandler.createResponse("/login.html"));
    }
}
