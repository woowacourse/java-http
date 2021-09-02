package nextstep.jwp.http.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpStatusTest {

    public static Stream<Arguments> createHttpStatusArgs() {
        return Stream.of(
                Arguments.of(200, HttpStatus.OK),
                Arguments.of(201, HttpStatus.CREATED),
                Arguments.of(204, HttpStatus.NO_CONTENT),
                Arguments.of(302, HttpStatus.FOUND),
                Arguments.of(400, HttpStatus.BAD_REQUEST),
                Arguments.of(403, HttpStatus.FORBIDDEN),
                Arguments.of(404, HttpStatus.NOT_FOUND),
                Arguments.of(500, HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("createHttpStatusArgs")
    void createHttpStatus(int statusCode, HttpStatus httpStatus) {
        assertThat(HttpStatus.valueOf(statusCode)).isEqualTo(httpStatus);
    }

    @Test
    void createNotExistingHttpStatus() {
        assertThatThrownBy(
                () -> HttpStatus.valueOf(5000)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
