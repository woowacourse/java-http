package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.apache.coyote.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String LOGIN_PAGE_PATH = "/login.html";
    private static final String MAIN_PAGE_PATH = "/index.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    public void handleGet(ServletRequest request, ServletResponse response) {
        response.sendRedirect(LOGIN_PAGE_PATH);
    }

    @Override
    public void handlePost(ServletRequest request, ServletResponse response) {

        final User findUser = InMemoryUserRepository.findByAccount(request.getParameter(ACCOUNT_KEY))
                .orElseThrow(() -> new UnauthorizedException(
                        "존재하지 않는 사용자 입니다 account: " + request.getParameter(ACCOUNT_KEY)));

        if (!findUser.checkPassword(request.getParameter(PASSWORD_KEY))) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다 account : " + findUser.getAccount());
        }

        log.info("로그인 성공! account : {}", findUser.getAccount());

        response.sendRedirect(MAIN_PAGE_PATH);
    }
}
