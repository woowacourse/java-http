package com.techcourse.controller;

import com.techcourse.session.Session;
import java.util.Map;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.startline.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.RestAssured;

class LoginControllerTest {

    @DisplayName("get 요청을 하면 로그인 페이지를 반환한다.")
    @Test
    void doGet() {
        RestAssured
                .when().get("/login")
                .then().assertThat()
                .httpStatusIs(HttpStatus.OK)
                .responseBodyContains("<title>로그인</title>");
    }

    @DisplayName("get 요청을 할 때, 이미 로그인 했다면 존재하면 index 페이지로 redirect한다.")
    @Test
    void doGet_loginned() {
        String session = RestAssured
                .when().post("/login")
                .body(Map.of("account", "gugu", "password", "password"))
                .then().assertThat()
                .httpStatusIs(HttpStatus.FOUND)
                .containsHeader(HttpHeader.LOCATION, "/index")
                .containsCookie(Session.KEY)
                .getFromCookies(Session.KEY)
                .get();

        RestAssured
                .when().get("/login")
                .cookie(Session.KEY, session)
                .then().assertThat()
                .httpStatusIs(HttpStatus.FOUND)
                .containsHeader(HttpHeader.LOCATION, "/index");
    }

    @DisplayName("post 요청을 하면 로그인하고, index 페이지로 redirect한다.")
    @Test
    void doPost() {
        RestAssured
                .when().post("/login")
                .body(Map.of("account", "gugu", "password", "password"))
                .then().assertThat()
                .httpStatusIs(HttpStatus.FOUND)
                .containsHeader(HttpHeader.LOCATION, "/index")
                .containsCookie("JSESSIONID");
    }

    @DisplayName("post 요청을 하면 로그인을 시도하고, 실패하면 401 페이지를 반환한다.")
    @Test
    void doPost_loginFail() {
        RestAssured
                .when().post("/login")
                .body(Map.of("account", "gugu", "password", "notPassword"))
                .then().assertThat()
                .httpStatusIs(HttpStatus.UNAUTHORIZED)
                .responseBodyContains("<p>Access to this resource is denied.</p>");
    }
}