package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Request Headers")
class RequestHeadersTest {

    @DisplayName("Headerd에서 Content-Length를 확인한다.")
    @Test
    void contentLength() {
        // given
        RequestHeaders requestHeaders = new RequestHeaders();
        String headerLine = "Content-Length: 80";
        requestHeaders.put(headerLine);

        // then
        int actual = requestHeaders.getContentLength();

        assertThat(actual).isEqualTo(80);
    }
}