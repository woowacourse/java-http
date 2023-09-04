package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseHeadersTest {

    @Test
    void of_메서드는_헤더_관련_데이터를_전달하면_HttpResponseHeaders를_초기화한다() {
        final ContentType contentType = ContentType.TEXT_HTML;
        final String body = "Hello World!";

        final HttpResponseHeaders actual = HttpResponseHeaders.of(contentType, body);

        assertThat(actual).isNotNull();
    }

    @Test
    void convertHeaders_메서드는_모든_헤더_데이터를_문자열로_변환해_반환한다() {
        final ContentType contentType = ContentType.TEXT_HTML;
        final String body = "Hello World!";
        final HttpResponseHeaders headers = HttpResponseHeaders.of(contentType, body);

        final String actual = headers.convertHeaders();

        assertThat(actual).isEqualTo("Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12 ");
    }
}
