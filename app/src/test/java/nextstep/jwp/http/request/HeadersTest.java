package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.Headers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HeadersTest {

    @Test
    void getHeader() {
        final Headers headers = new Headers();

        headers.putHeader("Host", "localhost:8080");
        headers.putHeader("Connection", "keep-alive");
        headers.putHeader("Accept", "*/*");

        final String actual = headers.getHeader("Host")
            .orElseThrow(IllegalArgumentException::new);

        assertThat(actual).isEqualTo("localhost:8080");
    }
}