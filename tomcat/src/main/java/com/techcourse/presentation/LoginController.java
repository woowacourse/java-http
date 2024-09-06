package com.techcourse.presentation;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static LoginController INSTANCE;

    private LoginController() {
    }

    public static LoginController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LoginController();
        }
        return INSTANCE;
    }

    public void getLogin(Request request, Response response) {
        if (request.existQueryParams()) {
            String account = request.getQueryParamValue("account");
            String password = request.getQueryParamValue("password");
            postLogin(account, password, response);
        }
        response.configureViewAndStatus("/login", StatusCode.OK);
    }

    public void postLogin(Request request, Response response) {
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");
        postLogin(account, password, response);
    }

    private void postLogin(String account, String password, Response response) {
        try {
            User user = getUser(account, password);
            log.info("로그인 성공! 아이디 : {}", user.getAccount());
            response.configureViewAndStatus("/index", StatusCode.FOUND);
        } catch (IllegalArgumentException e) {
            response.configureViewAndStatus("/401", StatusCode.UNAUTHORIZED);
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
