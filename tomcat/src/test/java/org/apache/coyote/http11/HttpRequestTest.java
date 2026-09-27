package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청_헤더의_값을_조회한다() {
        // given
        final HttpRequest request = createRequest(
            List.of("Cookie: JSESSIONID=session-id"));

        // when
        final String cookie = request.headerValueOf("Cookie");

        // then
        assertThat(cookie).isEqualTo("JSESSIONID=session-id");
    }

    private HttpRequest createRequest(final List<String> headerLines) {
        return new HttpRequest(
            new RequestLine(HttpMethod.GET, "/login", HttpVersion.VERSION_11),
            HttpHeaders.from(headerLines),
            "");
    }
}
