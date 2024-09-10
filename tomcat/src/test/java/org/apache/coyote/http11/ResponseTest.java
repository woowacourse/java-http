package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    @DisplayName("응답 객체 문자열 확인")
    void toHttpResponse() {
        //given
        final Response response = new Response(HttpStatusCode.OK);
        response.putHeader("Location", "http://localhost:8080/");

        //when && then
        assertThat(response.toHttpResponse()).isEqualTo("HTTP/1.1 200 OK \r\n" +
                                                        "Location: http://localhost:8080/ \r\n" +
                                                        "\r\n");
    }
}
