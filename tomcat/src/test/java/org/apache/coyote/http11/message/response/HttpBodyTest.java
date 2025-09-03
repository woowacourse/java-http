package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.message.HttpBody;
import org.junit.jupiter.api.Test;

class HttpBodyTest {

    @Test
    void 문자열로부터_HttpBody를_생성한다() {
        // given
        String bodyString = "Hello, world!";

        // when
        HttpBody httpBody = HttpBody.from(bodyString);

        // then
        assertAll(
                () -> assertThat(httpBody.toText()).isEqualTo(bodyString),
                () -> assertThat(httpBody.length()).isEqualTo(bodyString.getBytes(StandardCharsets.UTF_8).length)
        );
    }

    @Test
    void 바이트_배열로부터_HttpBody를_생성한다() {
        // given
        String bodyString = "Hello, world!";
        byte[] bodyBytes = bodyString.getBytes(StandardCharsets.UTF_8);

        // when
        HttpBody httpBody = HttpBody.from(bodyBytes);

        // then
        assertAll(
                () -> assertThat(httpBody.toText()).isEqualTo(bodyString),
                () -> assertThat(httpBody.length()).isEqualTo(bodyBytes.length)
        );
    }

    @Test
    void 빈_HttpBody를_생성한다() {
        // when
        HttpBody httpBody = HttpBody.empty();

        // then
        assertAll(
                () -> assertThat(httpBody.toText()).isEmpty(),
                () -> assertThat(httpBody.length()).isZero()
        );
    }

    @Test
    void toText는_바디를_문자열로_변환한다() {
        // given
        String expected = "some content";
        HttpBody httpBody = HttpBody.from(expected);

        // when
        String actual = httpBody.toText();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void length는_바디의_길이를_반환한다() {
        // given
        String content = "some content";
        HttpBody httpBody = HttpBody.from(content);

        // when
        int length = httpBody.length();

        // then
        assertThat(length).isEqualTo(content.getBytes(StandardCharsets.UTF_8).length);
    }
}
