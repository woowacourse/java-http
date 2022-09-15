package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseHeadersTest {

    @DisplayName("응답 헤더를 응답 반환 형식으로 변환한다")
    @Test
    void toResponse() {
        final HttpResponseHeaders headers = new HttpResponseHeaders();
        headers.add("Content-Type", ContentType.HTML.getValue());
        headers.add("Location", "/index.html");
        final String expected = "Content-Type: text/html;charset=utf-8 \r\nLocation: /index.html \r\n";

        final String actual = headers.toResponse();

        assertThat(actual).isEqualTo(expected);
    }
}
