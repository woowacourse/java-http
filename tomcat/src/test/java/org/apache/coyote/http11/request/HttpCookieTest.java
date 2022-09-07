package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpCookieTest {

    @Test
    void cookies를_파싱한다() {
        // given
        String rawCookies = "edenCookie=eden; morakCookie=morak";

        // when
        HttpCookie httpCookie = HttpCookie.of(rawCookies);

        // then
        assertThat(httpCookie.get("edenCookie")).isEqualTo("eden");
    }
}
