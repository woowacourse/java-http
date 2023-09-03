package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(result.getHeader().size()).isEqualTo(2);
        assertThat(result.getHeader().get("Host")).isEqualTo("localhost:8080");
        assertThat(result.getHeader().get("Connection")).isEqualTo("keep-alive");
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
        assertThat(result).contains("Content-Type: text/html");
        assertThat(result).contains("Content-Length: 5536");
    }
}
