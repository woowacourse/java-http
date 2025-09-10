package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.apache.catalina.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PAGE_PATH = "/login.html";
    private static final String MAIN_PAGE_PATH = "/index.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    public void doGet(ServletRequest request, ServletResponse response) {
        if (isLoginUser(request)) {
            response.sendRedirect(MAIN_PAGE_PATH);
            return;
        }
        response.sendRedirect(LOGIN_PAGE_PATH);
    }

    @Override
    public void doPost(ServletRequest request, ServletResponse response) {
        final User findUser = InMemoryUserRepository.findByAccount(request.getParameter(ACCOUNT_KEY))
                .orElseThrow(() -> new UnauthorizedException(
                        "존재하지 않는 사용자 입니다 account: " + request.getParameter(ACCOUNT_KEY)));

        if (!findUser.checkPassword(request.getParameter(PASSWORD_KEY))) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다 account : " + findUser.getAccount());
        }

        log.info("로그인 성공! account : {}", findUser.getAccount());

        final Session session = request.getSession(true);
        session.setAttribute("user", findUser);

        response.sendRedirect(MAIN_PAGE_PATH);
    }

    private boolean isLoginUser(ServletRequest request) {
        final Session session = request.getSession(false);
        if (session == null) {
            return false;
        }

        final Object userAttribute = session.getAttribute("user");
        if (userAttribute == null) {
            return false;
        }
        return userAttribute instanceof User;
    }
}
