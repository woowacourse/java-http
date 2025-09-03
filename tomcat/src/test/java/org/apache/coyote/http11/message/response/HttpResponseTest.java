package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void HttpResponse를_생성한다() {
        // given
        HttpStatus status = HttpStatus.OK;
        HttpHeaders headers = HttpHeaders.fromLines(List.of("Content-Type: text/plain"));
        HttpBody body = HttpBody.fromString("response body");

        // when & then
        assertThatCode(() -> new HttpResponse(status, headers, body))
                .doesNotThrowAnyException();
    }
}
