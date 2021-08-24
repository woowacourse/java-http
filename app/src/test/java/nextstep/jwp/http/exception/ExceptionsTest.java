package nextstep.jwp.http.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import jdk.jshell.spi.ExecutionControlProvider;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExceptionsTest {

    @ParameterizedTest
    @MethodSource("parametersForFindResponseByException")
    void findResponseByException(Exception e, int code) {
        final HttpResponse response = Exceptions.findResponseByException(e);

        assertThat(response.asString()).contains(String.valueOf(code));
    }

    private static Stream<Arguments> parametersForFindResponseByException() {
        return Stream.of(
            Arguments.of(new IllegalArgumentException(), 500),
            Arguments.of(new NotFoundException(), 404)
        );
    }
}