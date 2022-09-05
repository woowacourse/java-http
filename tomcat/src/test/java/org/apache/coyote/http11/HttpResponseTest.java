package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void HttpResponse를_형식에_맞게_문자열로_변환한다() {
        // given
        String requestBody = "request body";
        Map<String, String> headers = Map.of("Content-Type", "text/html", "Content-Length",
                String.valueOf(requestBody.getBytes().length));
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, headers, requestBody);

        // when
        String responseString = httpResponse.toResponseFormat();

        // then
        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + requestBody.getBytes().length + "\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "request body";
        assertThat(responseString).isEqualTo(expected);
    }
}
