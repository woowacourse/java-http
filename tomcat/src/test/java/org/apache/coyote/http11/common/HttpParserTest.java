package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.HttpFormatException;
import org.junit.jupiter.api.Test;

class HttpParserTest {

    @Test
    void query_string_형식에_맞게_파싱한다() {
        // given
        final var str = "account=ellie&password=password";

        // when
        final var parameters = HttpParser.parseQueryString(str);

        // then
        assertAll(
                () -> assertThat(parameters.get("account")).isEqualTo("ellie"),
                () -> assertThat(parameters.get("password")).isEqualTo("password")
        );
    }

    @Test
    void query_string_형식에_맞지_않는_경우_예외를_던진다() {
        // given
        final var str = "account=ellie&password";

        // when & then
        assertThatThrownBy(() -> HttpParser.parseQueryString(str))
                .isInstanceOf(HttpFormatException.class);
    }
}
