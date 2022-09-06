package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.request.RequestHeaders;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    void getContentLength() {
        // given
        final RequestHeaders headers = new RequestHeaders(Map.of("Content-Length", "1"));

        // when
        final int actual = headers.getContentLength();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void getContentLength_notExistsContentLength() {
        // given
        final RequestHeaders headers = new RequestHeaders(Map.of("Content-Type", "text/html"));

        // when
        final int actual = headers.getContentLength();

        // then
        assertThat(actual).isEqualTo(0);
    }
}
