package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void toMessage() {
        // given
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = "Hello World!";

        final HttpResponse httpResponse = HttpResponse.of(statusLine, contentType, responseBody);

        final String expectMessage = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                "Hello World!"
        );

        // when
        final String actual = httpResponse.toMessage();

        // then
        assertThat(actual).isEqualTo(expectMessage);
    }
}
