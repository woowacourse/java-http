package nextstep.org.apache.coyote.http11.http11.request.requestline;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.domain.request.requestline.HttpVersion;
import org.junit.jupiter.api.Test;

public class HttpVersionTest {

    @Test
    void getVersionTest() {
        String message =  "HTTP/0.9";

        assertThat(HttpVersion.from(message)).isEqualTo(HttpVersion.HTTP_09);
    }
}
