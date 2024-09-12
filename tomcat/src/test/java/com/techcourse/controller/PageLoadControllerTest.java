package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.RequestLine;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PageLoadControllerTest {

    @Nested
    @DisplayName("관련된 컨트롤러 여부")
    class isMatchesRequest {
        @ParameterizedTest
        @ValueSource(strings = {"/index.html", "/login.html", "/register.html"})
        @DisplayName("성공 : 연관된 path일 경우 true 반환")
        void isMatchesRequestReturnTrue(String path) {
            HttpRequest httpRequest = new HttpRequest(
                    new RequestLine("GET " + path + " HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );
            PageLoadController pageLoadController = new PageLoadController();

            boolean actual = pageLoadController.isMatchesRequest(httpRequest);

            assertThat(actual).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"login.html", "/index2.htm"})
        @DisplayName("성공 : 연관되지 않은 path일 경우 false 반환")
        void isMatchesRequestReturnFalse(String path) {
            HttpRequest httpRequest = new HttpRequest(
                    new RequestLine("GET " + path + " HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );
            PageLoadController pageLoadController = new PageLoadController();

            boolean actual = pageLoadController.isMatchesRequest(httpRequest);

            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Get 메서드 요청")
    class doGet {

        @ParameterizedTest
        @ValueSource(strings = {"/index.html", "/login.html", "/register.html"})
        @DisplayName("성공 : 존재하는 페이지일 경우 해당 페이지 response 반환")
        void doGetSuccessLoginSuccess(String path) throws IOException {
            HttpRequest request = new HttpRequest(
                    new RequestLine("GET " + path + " HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );
            HttpResponse response = HttpResponse.of(request);

            new PageLoadController().doGet(request, response);

            final URL resource = getClass().getClassLoader().getResource("static/" + path);
            byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + bytes.length + " \r\n" +
                    "\r\n" +
                    new String(bytes);

            String actual = response.toString();
            assertThat(actual).isEqualTo(expected);
        }
    }
}
