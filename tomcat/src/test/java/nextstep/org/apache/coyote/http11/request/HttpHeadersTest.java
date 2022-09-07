package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.apache.coyote.http11.request.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpHeadersTest {

    @DisplayName("Http 헤더 속성으로 http 문자를 잘 만드는지 확인한다.")
    @Test
    void toTextHeader() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(Collections.emptyMap());
        httpHeaders.setContentType("text/html");
        httpHeaders.setContentLength(12);
        httpHeaders.setLocation("/index.html");
        final String expected = "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 12 \r\n"
                + "Location: /index.html \r\n";

        // when
        final String actual = httpHeaders.toTextHeader();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
