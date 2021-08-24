package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.Headers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HeadersTest {

    @ParameterizedTest
    @MethodSource("parametersFroGetHeaders")
    void getHeader(String httpHeader) {
        Headers headers = new Headers(httpHeader);

        for (var entry : Fixture.getFixtureHeaders().entrySet()) {
            String headerName = entry.getKey();
            String headerValue = entry.getValue();

            String actual = headers.getHeader(headerName)
                .orElseThrow(IllegalArgumentException::new);

            assertThat(actual).isEqualTo(headerValue);
        }
    }

    private static Stream<Arguments> parametersFroGetHeaders() {
        return Stream.of(
            Arguments.of(Fixture.getHttpRequest()),
            Arguments.of(Fixture.postHttpRequest("test")),
            Arguments.of(Fixture.deleteHttpRequest()),
            Arguments.of(Fixture.putHttpRequest("test"))
        );
    }
}