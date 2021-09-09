package nextstep.jwp.http.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {

    public static Stream<Arguments> httpMethod() {
        return Stream.of(
                Arguments.of("GET", HttpMethod.GET),
                Arguments.of("Get", HttpMethod.GET),
                Arguments.of("POST", HttpMethod.POST),
                Arguments.of("post", HttpMethod.POST),
                Arguments.of("PUT", HttpMethod.PUT),
                Arguments.of("DELETE", HttpMethod.DELETE)
        );
    }

    @ParameterizedTest
    @MethodSource("httpMethod")
    void httpMethodCreate(String methodName, HttpMethod httpMethod) {
        assertThat(HttpMethod.of(methodName)).isEqualTo(httpMethod);
    }

    @Test
    void notExistingMethodCreate() {
        assertThatThrownBy(
                () -> HttpMethod.of("NOT_METHOD")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
