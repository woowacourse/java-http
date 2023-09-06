package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpExtensionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {

    private static final String CONTENT = "This Content's Length is 27";

    @DisplayName("ResponseBoody를 통해 ResponseHeader를 생성할 수 있다.")
    @Test
    void from() {
        // given
        final String contentLength = String.valueOf(CONTENT.length());
        final ResponseBody responseBody = ResponseBody.html(CONTENT);

        // when
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

        // then
        assertAll(
                () -> assertThat(responseHeader.getValue("Content-Type")).isEqualTo(
                        HttpExtensionType.HTML.getContentType()),
                () -> assertThat(responseHeader.getValue("Content-Length")).isEqualTo(contentLength)
        );
    }

    @DisplayName("ResponseHeader에 쿠키를 추가할 수 있다.")
    @Test
    void addCookie() {
        final ResponseBody responseBody = ResponseBody.html(CONTENT);
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);
        final HttpCookie cookie = HttpCookie.from("cookie1=one; cookie2=two; cookie3=three");

        // when & then
        assertAll(
                () -> assertThat(responseHeader.getValue("Set-Cookie")).isEmpty(),
                () -> responseHeader.addCookie(cookie),
                () -> assertThat(responseHeader.getValue("Set-Cookie")).isNotEmpty()
        );
    }

    @DisplayName("header에서 key에 매핑되는 value를 올바르게 반환할 수 있다.")
    @Test
    void getValue() {
        // given
        final ResponseBody responseBody = ResponseBody.html(CONTENT);
        final String expected = HttpExtensionType.HTML.getContentType();

        // when
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);
        final String actual = responseHeader.getValue("Content-Type");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("header에서 key에 매핑되는 value를 올바르게 반환할 수 있다.")
    @Test
    void getValue_nullKey() {
        // given
        final ResponseBody responseBody = ResponseBody.html(CONTENT);
        final String expected = "";

        // when
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);
        final String actual = responseHeader.getValue("null-key");

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
