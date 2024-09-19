package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserRegistration;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            register(request.getRequestBody());
            response.redirectTo(INDEX_PAGE);
        } catch (IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
            response.redirectTo(REGISTER_PAGE);
        }
    }

    private void register(Map<String, String> requestBody) {
        Optional<String> account = Optional.ofNullable(requestBody.get(ACCOUNT_KEY));
        Optional<String> password = Optional.ofNullable(requestBody.get(PASSWORD_KEY));
        Optional<String> email = Optional.ofNullable(requestBody.get(EMAIL_KEY));

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("필수 입력값이 부족합니다.");
        }

        validateRegistration(account.get(), password.get(), email.get());
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
        response.redirectTo(REGISTER_PAGE);
    }
}
