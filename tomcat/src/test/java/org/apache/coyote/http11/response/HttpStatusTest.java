package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.apache.coyote.http11.response.HttpStatus;
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

    @ParameterizedTest
    @MethodSource("provideRedirectCheck")
    void redirect인지_확인한다(HttpStatus httpStatus, boolean isRedirect) {
        // given & then
        assertThat(httpStatus.isRedirect()).isEqualTo(isRedirect);
    }

    public static Stream<Arguments> provideStatusForStatusCode() {
        return Stream.of(
                Arguments.of(HttpStatus.OK, HttpStatus.OK.getStatusCode()),
                Arguments.of(HttpStatus.REDIRECT, HttpStatus.REDIRECT.getStatusCode())
        );
    }

    public static Stream<Arguments> provideRedirectCheck() {
        return Stream.of(
                Arguments.of(HttpStatus.OK, false),
                Arguments.of(HttpStatus.REDIRECT, true)
        );
    }
}
