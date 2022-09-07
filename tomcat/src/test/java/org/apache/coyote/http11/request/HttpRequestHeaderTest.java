package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @DisplayName(value = "Content-Length의 크기를 찾는다")
    @Test
    void findContentLength() {
        // given
        final int contentLength = 100;
        final List<String> requestHeaders = List.of("Content-Length: " + contentLength);

        // when
        final HttpRequestHeader httpRequestHeader = new HttpRequestHeader(requestHeaders);

        // then
        assertThat(httpRequestHeader.findContentLength()).isEqualTo(contentLength);
    }

    @DisplayName(value = "JSESSIONID 값 찾기")
    @Test
    void name() {
        // given
        final String jSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        final HttpRequestHeader requestHeader = new HttpRequestHeader(
                List.of("Cookie: tasty_cookie=strawberry; JSESSIONID=" + jSessionId));

        // when
        Optional<String> actual = requestHeader.findJSessionId();

        // then
        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).isEqualTo(jSessionId);
    }
}
