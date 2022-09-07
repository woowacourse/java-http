package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Response 는 ")
class ResponseTest {

    @DisplayName("원하는 응답값을 생성한다.")
    @Test
    void createResponse() {
        final Response response = Response.builder(HttpVersion.HTTP11)
                .build();

        final String actual = response.setContentType(MediaType.TEXT_CSS)
                .setContentLength(11)
                .setBody("Hello world!")
                .getResponse();

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 11 ",
                "Content-Type: text/css;charset=utf-8 ",
                "",
                "Hello world!");

        assertThat(actual).isEqualTo(expected);
    }
}
