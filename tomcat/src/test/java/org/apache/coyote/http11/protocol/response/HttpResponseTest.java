package org.apache.coyote.http11.protocol.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.protocol.cookie.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse 를 생성한다.")
    void status() {
        HttpStatus httpStatus = HttpStatus.OK;
        String contentType = "text/html;charset=utf-8";
        String messageBody = "Hello, World!";

        HttpResponse httpResponse = HttpResponse.status(httpStatus)
                .header("Content-Type", contentType)
                .body(messageBody)
                .build();

        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(httpStatus),
                () -> assertThat(httpResponse.getResponseHeader("Content-Type")).isEqualTo(contentType),
                () -> assertThat(httpResponse.getMessageBody()).isEqualTo(messageBody)
        );
    }

    @Test
    @DisplayName("리디렉션 응답을 생성한다.")
    void redirect() {
        String location = "/new-location";
        HttpResponse httpResponse = HttpResponse.redirect(location).build();

        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getResponseHeader("Location")).isEqualTo(location)
        );
    }

    @Test
    @DisplayName("쿠키를 설정한다.")
    void setCookie() {
        HttpResponse httpResponse = HttpResponse.ok();
        Cookie cookie = new Cookie();
        cookie.setValue("sessionId", "abc123");

        httpResponse.setCookie(cookie);

        assertThat(httpResponse.getResponseHeader("Set-Cookie")).isEqualTo("sessionId=abc123");
    }

    @Test
    @DisplayName("메시지 본문을 설정하고 Content-Length 를 설정한다.")
    void setMessageBody() {
        HttpResponse httpResponse = HttpResponse.ok();
        String messageBody = "Hello, World!";

        httpResponse.setMessageBody(messageBody);

        assertAll(
                () -> assertThat(httpResponse.getMessageBody()).isEqualTo(messageBody),
                () -> assertThat(httpResponse.getResponseHeader("Content-Length"))
                        .isEqualTo(String.valueOf(messageBody.getBytes().length))
        );
    }

    @Test
    @DisplayName("기본 상태 코드 200 응답을 생성한다.")
    void ok() {
        HttpResponse httpResponse = HttpResponse.ok();

        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }
}
