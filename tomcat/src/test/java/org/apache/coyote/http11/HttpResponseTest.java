package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @DisplayName("응답을 문자열 형태로 파싱하여 반환한다.")
    @Test
    void should_parseResponse_when_getResponse() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.addHeader("key1", "value1");
        response.addHeader("key2", "value2");
        response.addHeader("key3", "value3");

        response.setBody("body");

        // then
        String expected = "HTTP/1.1 200 OK \r\n" +
                "key1: value1 \r\n" +
                "key2: value2 \r\n" +
                "key3: value3 \r\n" +
                "\r\n" +
                "body";
        assertThat(response.parseResponse()).isEqualTo(expected);
    }

    @DisplayName("상태를 401로 변경한다.")
    @Test
    void should_changeStatus401_whenSetStatusUnauthorized() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setStatusUnauthorized();

        // then
        String expected = "HTTP/1.1 401 UNAUTHORIZED";
        assertThat(response.parseResponse()).isEqualTo(expected);
    }

    @DisplayName("리다이렉트를 위한 상태로 변경한다.")
    @Test
    void should_changeStatus_whenSendRedirection() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.sendRedirection("/default.html");

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /default.html";
        assertThat(response.parseResponse()).isEqualTo(expected);
    }
}
