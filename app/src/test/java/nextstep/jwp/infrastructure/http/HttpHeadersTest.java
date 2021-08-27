package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("HTTP 헤더 빌더 테스트")
    @Test
    void HttpHeadersBuilderTest() {
        // given
        HttpHeaders headers = new HttpHeaders.Builder()
            .header("Content-Type", "text/html;charset=utf-8")
            .header("Content-Length", "13")
            .build();

        // when

        assertThat(headers.getValue("Content-Type")).isEqualTo(Collections.singletonList("text/html;charset=utf-8"));
        assertThat(headers.getValue("Content-Length")).isEqualTo(Collections.singletonList("13"));
    }
}