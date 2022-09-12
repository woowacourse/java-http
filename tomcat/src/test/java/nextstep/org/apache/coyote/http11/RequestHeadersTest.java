package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.request.RequestHeaders;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class RequestHeadersTest {

    @Test
    void getContentLength() throws IOException {
        // given
        final RequestHeaders headers = RequestFixture.createHeader(Map.of("Content-Length", "1"));

        // when
        final int actual = headers.getContentLength();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void getContentLength_notExistsContentLength() throws IOException {
        // given
        final RequestHeaders headers = RequestFixture.createHeader(Map.of("Content-Type", "text/html"));

        // when
        final int actual = headers.getContentLength();

        // then
        assertThat(actual).isEqualTo(0);
    }
}
