package com.techcourse.controller;

import com.techcourse.exception.DuplicateAccountException;
import com.techcourse.service.ApplicationService;
import java.util.Map;
import org.apache.catalina.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public ControllerResult login(Map<String, String> params, Session session) {
        String account = params.get("account");
        String password = params.get("password");

        var sessionUser = session.getAttribute("user");
        if (sessionUser != null) {
            return new ControllerResult.Redirect("/index.html");
        }

        if (account != null && password != null) {
            var user = applicationService.login(account, password);
            if (user.isPresent()) {
                log.info("Login successful: account={}", user.get().getAccount());
                session.setAttribute("user", user.get());
                return new ControllerResult.Redirect("/index.html");
            }
            return new ControllerResult.Redirect("/401.html");
        }

        return new ControllerResult.View("/login.html");
    }

    public ControllerResult register(Map<String, String> params) {

        if(params.get("account") != null && params.get("password") != null && params.get("email") != null) {
            try {
                applicationService.register(params.get("account"), params.get("password"), params.get("email"));
                return new ControllerResult.Redirect("/index.html");
            } catch (DuplicateAccountException e) {
                return new ControllerResult.View("/register.html");
            }
        }

        return new ControllerResult.View("/register.html");
    }
}
