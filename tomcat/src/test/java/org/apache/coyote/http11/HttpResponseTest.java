package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("HTTP response 값을 생성한다.")
    @Test
    void build() {
        // given
        String path = "index.html";
        String body = "body contents";
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body
        );

        // when
        String actual = HttpResponse
                .builder()
                .statusCode(HttpStatusCode.OK)
                .responseBody(body)
                .build();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}