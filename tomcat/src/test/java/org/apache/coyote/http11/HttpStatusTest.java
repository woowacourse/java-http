package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpStatusTest {

    @ParameterizedTest
    @MethodSource("provideStatusForStatusCode")
    void 알맞은_status_code를_응답한다(HttpStatus httpStatus, int statusCode) {
        // given & then
        assertThat(httpStatus.getStatusCode()).isEqualTo(statusCode);
    }

    public static Stream<Arguments> provideStatusForStatusCode() {
        return Stream.of(
                Arguments.of(HttpStatus.OK, HttpStatus.OK.getStatusCode()),
                Arguments.of(HttpStatus.REDIRECT, HttpStatus.REDIRECT.getStatusCode())
        );
    }
}
