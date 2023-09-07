package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeadersTest {

    @Test
    void add() {
        //given
        final var headers = new Headers();

        //when
        headers.put("key", "value");

        //then
        String actual = headers.get("key");
        assertThat(actual).isEqualTo("value");
    }

    @Test
    void noValue() {
        //given
        final var headers = new Headers();

        //when

        //then
        String actual = headers.get("key");
        assertThat(actual).isNull();
    }

}
