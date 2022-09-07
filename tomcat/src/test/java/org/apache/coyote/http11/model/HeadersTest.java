package org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @DisplayName("쿠키가 있을 때, 쿠키를 올바르게 파싱한다")
    @Test
    void generateCookieFromHeadersInput() {
        final var account = "gugu";
        final var password = "password";

        final var input = List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: account=gugu; password=password"
        );

        final var headers = Headers.of(input);

        assertAll(
                () -> assertThat(headers.getCookie().getValue("account")).isEqualTo(account),
                () -> assertThat(headers.getCookie().getValue("password")).isEqualTo(password)
        );
    }
}
