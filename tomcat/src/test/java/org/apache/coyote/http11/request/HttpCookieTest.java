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

    @ParameterizedTest
    @MethodSource("provideJSessionId")
    void JSESSIONID가_있는지_확인한다(String rawCookies, boolean expected) {
        // given
        HttpCookie httpCookie = HttpCookie.of(rawCookies);

        // then
        assertThat(httpCookie.existsJSessionId()).isEqualTo(expected);
    }

    public static Stream<Arguments> provideJSessionId() {
        return Stream.of(
                Arguments.of("JSESSIONID=eden-id", true),
                Arguments.of("edenCookie=eden", false)
        );
    }

}
