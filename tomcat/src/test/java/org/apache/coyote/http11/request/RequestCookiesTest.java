package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestCookiesTest {

    @DisplayName("객체 생성 시 헤더 내에서 쿠키를 추출한다.")
    @Test
    void should_extractCookies_when_headersGiven() throws IOException {
        // given
        String input = "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Accept: */*\r\n"
                + "Cookie: key1=value1; key2=value2\r\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        RequestHeaders headers = new RequestHeaders(reader);

        // when
        RequestCookies requestCookies = new RequestCookies(headers);

        // then
        assertThat(requestCookies.get("key1").get().getValue()).isEqualTo("value1");
        assertThat(requestCookies.get("key2").get().getValue()).isEqualTo("value2");
    }
}
