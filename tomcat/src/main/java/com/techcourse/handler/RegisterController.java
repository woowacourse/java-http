package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PAGE_PATH = "/register.html";
    private static final String MAIN_PAGE_PATH = "/index.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    @Override
    public void doGet(ServletRequest request, ServletResponse response) {
        response.sendRedirect(REGISTER_PAGE_PATH);
    }

    @Override
    public void doPost(ServletRequest request, ServletResponse response) {
        final String account = request.getParameter(ACCOUNT_KEY);
        final String password = request.getParameter(PASSWORD_KEY);
        final String email = request.getParameter(EMAIL_KEY);

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입 성공! account : {}", account);

        response.sendRedirect(MAIN_PAGE_PATH);
    }
}
