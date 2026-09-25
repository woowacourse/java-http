package org.apache.coyote.http.response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.apache.coyote.http.HttpVersion;
import org.junit.jupiter.api.Test;

class StatusLineTest {

    @Test
    void 버전_상태_코드_메시지를_가진_상태_라인을_생성한다() {
        final StatusLine statusLine = StatusLine.of(HttpVersion.HTTP_1_1, HttpStatus.NOT_FOUND);

        assertThat(statusLine.value()).isEqualTo("HTTP/1.1 404 Not Found");
    }
}
