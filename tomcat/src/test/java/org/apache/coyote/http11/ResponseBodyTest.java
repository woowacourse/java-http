package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ResponseBodyTest {

    @Test
    void testCreatedWithProperValues() {
        //given
        final var body = "Hello world!";

        //when
        final var responseBody = ResponseBody.from(body);

        //then
        assertAll(
                () -> assertThat(body).isEqualTo(responseBody.getBody()),
                () -> assertThat(responseBody.getContentLength()).hasToString("Content-Length: 12")
        );
    }

    @Test
    void testIsEmptyWhenBodyStringIsEmpty() {
        //given
        final var body = "";

        //when
        final var responseBody = ResponseBody.from(body);

        //then
        assertThat(responseBody.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "   ", "\n"})
    void testIsEmptyWhenBodyStringIsNotEmpty(String body) {
        //when
        final var responseBody = ResponseBody.from(body);

        //then
        assertThat(responseBody.isEmpty()).isFalse();
    }

}
