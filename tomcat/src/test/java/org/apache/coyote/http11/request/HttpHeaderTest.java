package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpHeaderTest {

    static Stream<Arguments> httpHeaderWithMatchedNameProvider() {
        return Stream.of(
                Arguments.of(
                        HttpHeader.LOCATION,
                        "Location"
                ),
                Arguments.of(
                        HttpHeader.CONTENT_LENGTH,
                        "Content-Length"
                ),
                Arguments.of(
                        HttpHeader.CONTENT_TYPE,
                        "Content-Type"
                ),
                Arguments.of(
                        HttpHeader.COOKIE,
                        "Cookie"
                ),
                Arguments.of(
                        HttpHeader.SET_COOKIE,
                        "Set-Cookie"
                )
        );
    }

    @DisplayName("HttpHeader 이름으로 HttpHeader를 찾을 수 있다.")
    @ParameterizedTest
    @MethodSource("httpHeaderWithMatchedNameProvider")
    void findByName(HttpHeader header, String name) {
        assertThat(HttpHeader.findByName(name)).isEqualTo(header);
    }
}
