package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import java.util.stream.Stream;
import org.apache.coyote.http11.http.domain.RequestTarget;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RequestTargetTest {

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("/", "/", Map.of()),
                Arguments.of("/index.html", "/index.html", Map.of()),
                Arguments.of("/login?account=gugu&password=password", "/login",
                        Map.of("account", "gugu", "password", "password"))
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void from(final String requestTargetValue, final String uri, final Map<String, String> queryParameters) {
        RequestTarget requestTarget = RequestTarget.from(requestTargetValue);

        assertAll(
                () -> assertThat(requestTarget.getUri()).isEqualTo(uri),
                () -> assertThat(requestTarget.getQueryParameters()).isEqualTo(queryParameters)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "index.html")
    void validateInvalidRequestTarget(final String input) {
        assertThatThrownBy(() -> RequestTarget.from(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid request-target");
    }
}
