package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.HttpFormatException;
import org.junit.jupiter.api.Test;

class HttpParserTest {

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
        final var header = HttpParser.parseHeaders(lines);

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
        final var header = HttpParser.parseHeaders(lines);

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
        assertThatThrownBy(() -> HttpParser.parseHeaders(lines))
                .isInstanceOf(HttpFormatException.class);
    }

    @Test
    void cookie_형식에_맞게_파싱한다() {
        // given
        final var str = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        final var cookie = HttpParser.parseCookie(str);

        // then
        assertAll(
                () -> assertThat(cookie.get("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @Test
    void cookie_형식에_맞지_않는_경우_예외를_던진다() {
        // given
        final var str = "yummy_cookie=choco; tasty_cookie";


        // when & then
        assertThatThrownBy(() -> HttpParser.parseCookie(str))
                .isInstanceOf(HttpFormatException.class);
    }

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
