package com.techcourse.controller;

import com.techcourse.service.ApplicationService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    public String login(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        if (account != null && password != null) {
            var user = applicationService.login(account, password);
            user.ifPresent(loginUser -> log.info("login user: {}", loginUser));
        }

        return "/login.html";
    }
}
