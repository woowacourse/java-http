package com.techcourse.controller;

import java.util.Map;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.startline.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.RestAssured;

class RegisterControllerTest {

    @DisplayName("get 요청을 하면 회원가입 페이지를 반환한다.")
    @Test
    void doGet() {
        RestAssured
                .when().get("/register")
                .then().assertThat()
                .httpStatusIs(HttpStatus.OK)
                .responseBodyContains("<title>회원가입</title>");
    }

    @DisplayName("post 요청을 하면 회원가입하고, 로그인하며 index 페이지로 redirect 한다.")
    @Test
    void doPost() {
        RestAssured
                .when().post("/register")
                .body(Map.of("account", "hoti", "password", "handsome", "email", "hothoti@mail.com"))
                .then().assertThat()
                .httpStatusIs(HttpStatus.FOUND)
                .containsHeader(HttpHeader.LOCATION, "/index");
    }
}