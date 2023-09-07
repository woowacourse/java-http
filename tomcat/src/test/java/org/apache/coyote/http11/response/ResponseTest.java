package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseTest {

    @Test
    void toMessage() {
        // given
        final StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = "Hello World!";

        final Response response = Response.of(startLine, contentType, responseBody);

        final String expectMessage = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                "Hello World!"
        );

        // when
        final String actual = response.toMessage();

        // then
        assertThat(actual).isEqualTo(expectMessage);
    }
}
