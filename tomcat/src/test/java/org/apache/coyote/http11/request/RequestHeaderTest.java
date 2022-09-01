package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.HttpFormatException;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {

    @Test
    void 문자열에서_Header를_파싱한다() {
        // given
        final var lines = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* "
        );

        // when
        final var header = RequestHeader.from(lines).getValue();

        // then
        assertAll(
                () -> assertThat(header).hasSize(5),
                () -> assertThat(header.get("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(header.get("Content-Length")).isEqualTo("80"),
                () -> assertThat(header.get("Accept")).isEqualTo("*/*")
        );
    }

    @Test
    void Header가_Blank일_경우_빈_Header로_초기화한다() {
        // given
        final var lines = " ";

        // when
        final var header = RequestHeader.from(lines).getValue();

        // then
        assertThat(header).hasSize(0);
    }

    @Test
    void Header의_형식이_잘못된_경우_예외를_던진다() {
        // given
        final var lines = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection"
        );

        // when & then
        assertThatThrownBy(() -> RequestHeader.from(lines))
                .isInstanceOf(HttpFormatException.class);
    }
}
