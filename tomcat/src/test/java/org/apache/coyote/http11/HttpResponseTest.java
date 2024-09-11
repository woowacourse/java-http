package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.component.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("응답 객체 문자열 확인")
    void toHttpResponse() throws IOException {
        //given
        final var stringReader = new StringReader(
                "GET / HTTP/1.1\r\nHost: localhost:8080 \r\n");
        final var bufferedReader = new BufferedReader(stringReader);
        final HttpRequest request = HttpRequest.from(bufferedReader);
        final HttpResponse response = new HttpResponse(HttpStatusCode.OK);
        response.putHeader("Location", "http://localhost:8080/");

        //when && then
        assertThat(response.toHttpResponse(request)).isEqualTo("HTTP/1.1 200 OK \r\n" +
                                                               "Location: http://localhost:8080/ \r\n" +
                                                               "\r\n");
    }
}
