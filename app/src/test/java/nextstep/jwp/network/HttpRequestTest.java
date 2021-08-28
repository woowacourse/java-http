package nextstep.jwp.network;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class HttpRequestTest {

    @Test
    void create() {
        // given
        final List<String> requestAsString = List.of(
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*"
        );

        // when // then
        assertThatCode(() -> HttpRequest.of(requestAsString))
                .doesNotThrowAnyException();
    }
}