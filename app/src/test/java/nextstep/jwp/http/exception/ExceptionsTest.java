package nextstep.jwp.http.exception;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @ParameterizedTest
    @MethodSource("parametersForFindResponseByException")
    void findResponseByException(Exception e, int code) {
        Exceptions exception = Exceptions.findByException(e);

        assertThat(exception.getHttpStatus().getCode()).isEqualTo(code);
    }

    private static Stream<Arguments> parametersForFindResponseByException() {
        return Stream.of(
            Arguments.of(new IllegalArgumentException(), 500),
            Arguments.of(new NotFoundException(), 404)
        );
    }
}
