package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @DisplayName("헤더 값을 추가한다.")
    @Test
    void addHeader() {
        // given
        HttpHeader httpHeader = HttpHeader.empty();

        // when
        httpHeader.add("key", "value");

        // then
        assertThat(httpHeader.get("key")).hasValue("value");
    }

    @DisplayName("이미 존재하는 헤더를 추가할 경우 기존 값 뒤에 추가된다.")
    @Test
    void addDuplicateHeaderValue() {
        // given
        HttpHeader httpHeader = HttpHeader.from("key: value1");

        // when
        httpHeader.add("key", "value2");

        // then
        assertThat(httpHeader.get("key")).hasValue("value1,value2");
    }

    @DisplayName("저장된 헤더 값들을 HTTP 형식으로 반환한다.")
    @Test
    void convertToHTTPString() {
        // given
        HttpHeader httpHeader = HttpHeader.empty();
        httpHeader.add("key1", "value1");
        httpHeader.add("key2", "value2");
        httpHeader.add("key2", "value3");

        // when
        String httpString = httpHeader.toString();

        // then
        assertThat(httpString).isEqualTo("key1: value1\r\nkey2: value2,value3");
    }

    @DisplayName("헤더에 Content-Length가 없는 경우 OptionalInt.empty()를 반환한다.")
    @Test
    void getContentLengthWhenNoContentLength() {
        // given
        HttpHeader httpHeader = HttpHeader.empty();

        // when
        var contentLength = httpHeader.getContentLength();

        // then
        assertThat(contentLength).isEmpty();
    }

    @DisplayName("헤더에 Content-Length가 있는 경우 값을 반환한다.")
    @Test
    void getContentLength() {
        // given
        HttpHeader httpHeader = HttpHeader.from("Content-Length: 10");

        // when
        var contentLength = httpHeader.getContentLength();

        // then
        assertThat(contentLength).hasValue(10);
    }

    @DisplayName("헤더에 Content-Type을 추가한다.")
    @Test
    void addContentType() {
        // given
        HttpHeader httpHeader = HttpHeader.empty();

        // when
        httpHeader.setContentType("text/html");

        // then
        assertThat(httpHeader.get("Content-Type")).hasValue("text/html");
    }

    @DisplayName("쿠키 값을 올바르게 가져온다.")
    @Test
    void getCookie() {
        // given
        HttpHeader httpHeader = HttpHeader.from("Cookie: key1=value1; key2=value2");

        // when & then
        assertAll(
                () -> assertThat(httpHeader.getCookie("key1")).hasValue("value1"),
                () -> assertThat(httpHeader.getCookie("key2")).hasValue("value2"),
                () -> assertThat(httpHeader.getCookie("key3")).isEmpty()
        );
    }
}
