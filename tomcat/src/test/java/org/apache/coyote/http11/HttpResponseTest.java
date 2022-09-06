package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        assertAll(
                () -> assertThat(responseString).contains("HTTP/1.1 200 OK"),
                () -> assertThat(responseString).contains("Content-Length: " + requestBody.getBytes().length),
                () -> assertThat(responseString).contains("Content-Type: text/html"),
                () -> assertThat(responseString).contains("request body")
        );
    }
}
