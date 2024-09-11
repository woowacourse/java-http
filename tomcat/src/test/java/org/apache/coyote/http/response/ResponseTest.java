package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.StatusCode;

class ResponseTest {

    @Test
    void Response의_bytes를_반환한다() {
        // given
        Response response = new Response();
        response.configureViewAndStatus("index.html", StatusCode.OK);
        response.setContentType(MimeType.HTML);
        response.setBody("Hello, World!");


        // when
        byte[] bytes = response.getBytes();

        // then
        String expected = """
                HTTP/1.1 200 OK \r
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 13 \r
                \r
                Hello, World!""";
        assertThat(bytes).isEqualTo(expected.getBytes());
    }
}
