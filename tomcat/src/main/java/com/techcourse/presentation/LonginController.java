package com.techcourse.presentation;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.ResponseAndView;

public class LonginController {

    private static final Logger log = LoggerFactory.getLogger(LonginController.class);

    public ResponseAndView getLoginPage(Request request) {
        String account = request.getQueryParamValue("account");
        String password = request.getQueryParamValue("password");
        User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("invalid password");
        }
        log.info("user : {}", user);
        return new ResponseAndView("/login", StatusCode.OK);
    }
}
