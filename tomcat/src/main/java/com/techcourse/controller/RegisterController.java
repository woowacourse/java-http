package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserRegistration;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            register(request.getRequestBody());
            redirect(INDEX_PAGE, response);

        } catch (IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
            redirect(REGISTER_PAGE, response);
        }
    }

    private void register(Map<String, String> requestBody) {
        String account = requestBody.get(ACCOUNT_KEY);
        String password = requestBody.get(PASSWORD_KEY);
        String email = requestBody.get(EMAIL_KEY);

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("필수 입력값이 부족합니다.");
        }

        validateRegistration(account, password, email);
    }

    private void validateRegistration(String account, String password, String email) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> {
                            throw new IllegalArgumentException("이미 존재하는 계정명입니다.");
                        },
                        () -> {
                            UserRegistration userRegisterInfo = new UserRegistration(account, password, email);
                            User newUser = InMemoryUserRepository.save(userRegisterInfo);
                            log.info("user: {}의 회원가입이 완료되었습니다.", newUser.getAccount());
                        });
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        redirect(REGISTER_PAGE, response);
    }
}
