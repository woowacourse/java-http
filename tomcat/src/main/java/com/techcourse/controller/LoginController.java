package com.techcourse.controller;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;

    public LoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean isProvidableUrl(final String path) {
        return "/login".equals(path);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpSession session = request.getSession();
        if (session == null) {
            log.info("no session detected in login");
            response.setOk("login.html");
            return;
        }

        final Object user = session.getAttribute("user");
        if (user instanceof User) {
            response.setFound("index.html");
            return;
        }
        response.setOk("login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        request.invalidateExistSession();

        final String account = request.getBodyElement("account");
        final String password = request.getBodyElement("password");

        try {
            final User user = loginService.login(account, password);
            response.setFound("index.html");
            response.addAttribute("user", user);
        } catch (UnauthorizedException e) {
            response.setUnauthorized();
        }
    }
}
