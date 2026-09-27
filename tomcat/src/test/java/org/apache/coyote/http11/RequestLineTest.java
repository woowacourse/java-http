package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void parseRequestLine() {
        //given
        String raw = "GET /login?account=gugu HTTP/1.1";

        //when
        RequestLine result = RequestLine.parse(raw);

        //then
        assertThat(result.method()).isEqualTo("GET");
        assertThat(result.path()).isEqualTo("/login");
        assertThat(result.query()).isEqualTo("account=gugu");
        assertThat(result.version()).isEqualTo("HTTP/1.1");
    }

    @Test
    void parseRequestLineNoQuery() {
        //given
        String raw = "GET /login HTTP/1.1";

        //when
        RequestLine result = RequestLine.parse(raw);

        //then
        assertThat(result.method()).isEqualTo("GET");
        assertThat(result.path()).isEqualTo("/login");
        assertThat(result.query()).isEqualTo("");
        assertThat(result.version()).isEqualTo("HTTP/1.1");
    }
}
