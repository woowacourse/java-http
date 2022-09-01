package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Response 는 ")
class ResponseTest {

    @DisplayName("원하는 응답값을 생성한다.")
    @Test
    void createResponse() {
        final String response = new Response.ResponseBuilder(HttpVersion.HTTP11, Status.OK)
                .setContentType(MediaType.TEXT_CSS)
                .setContentLength(11)
                .setBody("Hello world!")
                .build()
                .getResponse();

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 11 ",
                "Content-Type: text/css;charset=utf-8 ",
                "",
                "Hello world!");

        assertThat(response).isEqualTo(expected);
    }
}
