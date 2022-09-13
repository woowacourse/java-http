package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.constant.HttpContent;
import org.apache.coyote.http11.constant.HttpHeader;
import org.apache.coyote.http11.constant.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.controller.ResponseHelper;

class ResponseTest {

    HttpResponse response;

    @BeforeEach
    void setUp() {
        response = new HttpResponse();
    }

    @DisplayName("response의 body를 설정한다.")
    @Test
    void body() {
        response.body("abcdefg");

        assertThat(response.toMessage()).contains("abcdefg");
    }

    @DisplayName("response의 status를 설정한다.")
    @Test
    void statusCode() {
        response.statusCode(HttpStatus.OK);

        assertThat(response.toMessage()).contains("200 OK");
    }

    @DisplayName("response의 header을 추가한다.")
    @Test
    void addHeader() {
        response.addHeader(HttpHeader.CONTENT_TYPE.value(), HttpContent.HTML.getContentType());

        assertThat(response.toMessage()).contains("Content-Type: " + HttpContent.HTML.getContentType());
    }

    @DisplayName("response에 Cookie를 넣는다.")
    @Test
    void addCookie() {
        response.addCookie("JSESSIONID", "123456789L");

        assertThat(response.toMessage()).contains("Cookie: JSESSIONID=123456789L");
    }

    @DisplayName("Resource를 불러온다.")
    @Test
    void loadResource() throws IOException {
        ResponseHelper responseHelper = new ResponseHelper();
        responseHelper.loadResource(response, "/index.html");

        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.toMessage()).contains(expected);
    }

    @DisplayName("일반적인 문자열을 Response에 실어 보낸다.")
    @Test
    void loadRawString() throws IOException {
        ResponseHelper responseHelper = new ResponseHelper();

        responseHelper.loadRawString(response, "abcdefgh");

        assertThat(response.toMessage()).contains("abcdefgh");
    }

    @DisplayName("Response 객체를 Http Response text로 변환한다.")
    @Test
    void toMessage() {
        response.statusCode(HttpStatus.OK);
        response.addHeader(HttpHeader.CONTENT_TYPE.value(), HttpContent.HTML.getContentType());
        response.body("abcdefgh");

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "",
                "abcdefgh");

        assertThat(response.toMessage()).isEqualTo(expected);
    }
}