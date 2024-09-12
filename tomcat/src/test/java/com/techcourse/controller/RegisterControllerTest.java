package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.HttpStatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    final InMemoryUserRepository inMemoryUserRepository = InMemoryUserRepository.getInstance();

    final RegisterController registerController = new RegisterController();

    @BeforeEach
    void setUp() {
        inMemoryUserRepository.clear();
    }

    @DisplayName("사용자를 등록하고 Location 헤더 값이 index.html로 302 상태 코드를 반환한다.")
    @Test
    void registerUser() {
        final String account = "mark";
        final String password = "1234";
        final String email = "email";
        HttpResponse response = new HttpResponse();
        HttpRequest request = new HttpRequest();
        request.setParameter("account", account);
        request.setParameter("password", password);
        request.setParameter("email", email);

        registerController.doPost(request, response);

        assertAll(
                () -> assertTrue(inMemoryUserRepository.findByAccount(account).isPresent()),
                () -> assertEquals(HttpStatusCode.FOUND, response.getHttpStatusCode()),
                () -> assertEquals("/index.html", response.getHeaders().get("Location"))
        );
    }

    @DisplayName("사용자 등록 시 account, password, email 중 하나라도 없으면 BadRequest를 반환한다.")
    @Test
    void registerUserBadRequest() {
        final String account = "mark";
        final String password = "1234";
        HttpRequest request = new HttpRequest();
        request.setParameter("account", account);
        request.setParameter("password", password);
        HttpResponse response = new HttpResponse();

        registerController.doPost(request, response);

        assertAll(
                () -> assertFalse(inMemoryUserRepository.findByAccount(account).isPresent()),
                () -> assertEquals(HttpStatusCode.BAD_REQUEST, response.getHttpStatusCode())
        );
    }
}
