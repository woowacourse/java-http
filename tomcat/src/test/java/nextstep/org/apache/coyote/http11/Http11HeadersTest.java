package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.Http11Headers;
import org.junit.jupiter.api.Test;

class Http11HeadersTest {

    @Test
    void getContentLength() {
        // given
        final Http11Headers http11Headers = new Http11Headers(Map.of("Content-Length", "1"));

        // when
        final int actual = http11Headers.getContentLength();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void getContentLength_notExistsContentLength() {
        // given
        final Http11Headers http11Headers = new Http11Headers(Map.of("Content-Type", "text/html"));

        // when
        final int actual = http11Headers.getContentLength();

        // then
        assertThat(actual).isEqualTo(0);
    }
}
