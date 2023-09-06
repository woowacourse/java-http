package org.apache.coyote.http11;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class HttpStatusTest {

    @ParameterizedTest
    @MethodSource("provideHttpStatus")
    void testToString(HttpStatus httpStatus, String expected) {
        //given
        //when
        String result = httpStatus.toString();

        //then
        assertThat(result).isEqualTo(expected);
    }

    public static Stream<Arguments> provideHttpStatus() {
        return Stream.of(
                Arguments.of(HttpStatus.OK, "200 OK"),
                Arguments.of(HttpStatus.FOUND, "302 Found"),
                Arguments.of(HttpStatus.UNAUTHORIZED, "401 Unauthorized"),
                Arguments.of(HttpStatus.NOT_FOUND, "404 Not Found")
        );
    }

}
