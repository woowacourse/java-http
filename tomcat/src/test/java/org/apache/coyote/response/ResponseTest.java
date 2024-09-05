package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    void Response의_bytes를_반환한다() {
        // given
        ResponseLine responseLine = new ResponseLine(StatusCode.OK);
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.contentType("text/html");
        responseHeaders.contentLength(1024);
        ResponseBody responseBody = new ResponseBody("Hello, World!");
        Response response = new Response(responseLine, responseHeaders, responseBody);

        // when
        byte[] bytes = response.getBytes();

        // then
        String expected = """
                HTTP/1.1 200 OK \r
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 1024 \r
                \r
                Hello, World!""";
        assertThat(bytes).isEqualTo(expected.getBytes());
    }
}
