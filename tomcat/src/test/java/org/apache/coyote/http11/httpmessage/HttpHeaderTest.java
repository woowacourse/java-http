package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    @DisplayName("header 정보 관련 문자열 배열이 들어오면 http header 객체를 만든다.")
    void make_http_header_from_string_array() {
        // given
        final String[] headers = new String[2];
        headers[0] = "Host: localhost:8080";
        headers[1] = "Connection: keep-alive";

        // when
        final HttpHeader result = HttpHeader.fromRequest(headers);

        // then
        assertThat(result.getHeader()).hasSize(2);
        assertThat(result.getHeader()).containsEntry("Host", "localhost:8080");
        assertThat(result.getHeader()).containsEntry("Connection", "keep-alive");
    }

    @Test
    @DisplayName("header 정보를 http 메시지 형식으로 변환한다.")
    void make_header_to_http_message_form() {
        // given
        final String[] headers = new String[2];
        headers[0] = "Content-Type: text/html";
        headers[1] = "Content-Length: 5536";
        final HttpHeader httpHeader = HttpHeader.fromRequest(headers);

        // when
        final String result = httpHeader.makeResponseForm();

        // then
        assertAll(
            () -> assertThat(result).contains("Content-Type: text/html"),
            () -> assertThat(result).contains("Content-Length: 5536")
        );
    }

    @Test
    @DisplayName("쿠키 정보를 반환한다.")
    void get_cookies() {
        // given
        final String[] headers = new String[3];
        headers[0] = "Content-Type: text/html";
        headers[1] = "Content-Length: 5536";
        headers[2] = "Cookie: JSESSIONID=ako";
        final HttpHeader httpHeader = HttpHeader.fromRequest(headers);

        // when
        final HttpCookie cookies = httpHeader.getCookies();

        // then
        assertThat(cookies.getCookies()).hasSize(1);
    }

    @Test
    @DisplayName("쿠키가 없으면 빈 map을 반환한다.")
    void get_empty() {
        // given
        final String[] headers = new String[2];
        headers[0] = "Content-Type: text/html";
        headers[1] = "Content-Length: 5536";
        final HttpHeader httpHeader = HttpHeader.fromRequest(headers);

        // when
        final HttpCookie cookies = httpHeader.getCookies();

        // then
        assertThat(cookies.getCookies().size()).isZero();
    }
}
