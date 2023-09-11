package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @Test
    void includeContentHeaderWhenExists() {
        //given
        final var headers = new Headers();
        headers.put("Content-Length", "12");
        headers.put("Content-Type", "text/plain;charset=utf-8");
        headers.put("key", "value");

        //when
        String actual = headers.join();

        //then
        assertAll(
                () -> assertThat(actual).startsWith("key: value "),
                () -> assertThat(actual).contains("Content-Length: 12 "),
                () -> assertThat(actual).contains("Content-Type: text/plain;charset=utf-8 ")
        );

    }

}
