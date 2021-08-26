package nextstep.jwp.http.header.request.request_line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import nextstep.jwp.http.exception.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {

    @DisplayName("String으로 들어온 HttpMethod를 파싱한다")
    @ParameterizedTest
    @MethodSource("parametersForFindMethod")
    void findMethod(String methodName, HttpMethod httpMethod) {
        HttpMethod method = HttpMethod.from(methodName);

        assertThat(method).isEqualTo(httpMethod);
    }

    private static Stream<Arguments> parametersForFindMethod() {
        return Stream.of(
            Arguments.of("GET", HttpMethod.GET),
            Arguments.of("get", HttpMethod.GET),
            Arguments.of("POST", HttpMethod.POST),
            Arguments.of("post", HttpMethod.POST),
            Arguments.of("DELETE", HttpMethod.DELETE),
            Arguments.of("delete", HttpMethod.DELETE),
            Arguments.of("PUT", HttpMethod.PUT),
            Arguments.of("put", HttpMethod.PUT)
        );
    }

    @DisplayName("잘못된 value가 들어오면 예외")
    @Test
    void findMethod_invalid_fail() {
        assertThatThrownBy(() -> HttpMethod.from("test"))
            .isInstanceOf(InternalServerException.class);
    }
}
