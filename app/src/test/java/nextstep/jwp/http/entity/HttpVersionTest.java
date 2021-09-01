package nextstep.jwp.http.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class HttpVersionTest {
    @Test
    void httpVersionCreate() {
        HttpVersion httpVersion = HttpVersion.of("HTTP/1.1");
        assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    void NotExistingHttpVersionCreate() {
        assertThatThrownBy(
                () -> HttpVersion.of("HTTP/NOT.EXISTING")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
