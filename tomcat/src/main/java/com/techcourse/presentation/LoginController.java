package com.techcourse.presentation;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.ResponseAndView;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public ResponseAndView getLoginPage(Request request) {
        if (request.existQueryParams()) {
            String account = request.getQueryParamValue("account");
            String password = request.getQueryParamValue("password");
            return login(account, password);
        }
        return new ResponseAndView("/login", StatusCode.OK);
    }

    private ResponseAndView login(String account, String password) {
        try {
            User user = getUser(account, password);
            log.info("user : {}", user);
            return new ResponseAndView("/index", StatusCode.FOUND);
        } catch (Exception e) {
            return new ResponseAndView("/401", StatusCode.UNAUTHORIZED);
        }
    }

    private User getUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정 또는 비밀번호가 일치하지 않습니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("계정 또는 비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
