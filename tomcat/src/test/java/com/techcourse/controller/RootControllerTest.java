package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.RequestLine;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RootControllerTest {

    @Nested
    @DisplayName("관련된 컨트롤러 여부")
    class isMatchesRequest {
        @ParameterizedTest
        @ValueSource(strings = {"/"})
        @DisplayName("성공 : 연관된 path일 경우 true 반환")
        void isMatchesRequestReturnTrue(String path) {
            HttpRequest httpRequest = new HttpRequest(
                    new RequestLine("GET " + path + " HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );
            RootController rootController = new RootController();

            boolean actual = rootController.isMatchesRequest(httpRequest);

            assertThat(actual).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"//"})
        @DisplayName("성공 : 연관되지 않은 path일 경우 false 반환")
        void isMatchesRequestReturnFalse(String path) {
            HttpRequest httpRequest = new HttpRequest(
                    new RequestLine("GET " + path + " HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );
            RootController rootController = new RootController();

            boolean actual = rootController.isMatchesRequest(httpRequest);

            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Get 메서드 요청")
    class doGet {

        @Test
        @DisplayName("성공 : 문자열 반환")
        void doGetSuccessLoginSuccess() {
            HttpRequest request = new HttpRequest(
                    new RequestLine("GET / HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );
            HttpResponse response = HttpResponse.of(request);

            new RootController().doGet(request, response);

            var expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 12 ",
                    "",
                    "Hello world!");

            String actual = response.toString();
            assertThat(actual).isEqualTo(expected);
        }
    }
}

