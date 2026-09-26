package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.util.StaticResources;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String BAD_REQUEST_MESSAGE = "잘못된 요청입니다.";
    private static final String PAGE_NOT_FOUND_MESSAGE = "서버 오류가 발생했습니다.";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        StaticResources.read(REGISTER_PAGE).ifPresentOrElse(
                response::okHtml,
                () -> response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, PAGE_NOT_FOUND_MESSAGE));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter(ACCOUNT);
        final String password = request.getParameter(PASSWORD);
        final String email = request.getParameter(EMAIL);

        if (account == null || password == null || email == null) {
            response.sendError(HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE);
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입 성공: {}", account);
        response.sendRedirect(INDEX_PAGE);
    }
}
