package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("response를 Http Message로 변환한다.")
    void toHttpMessage() {
        final HttpResponse httpResponse = new HttpResponse(
                ContentType.TEXT_PLAIN,
                HttpStatus.OK,
                new HttpHeaders(new ArrayList<>()),
                "Hello world!"
        );

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(httpResponse.toHttpMessage()).isEqualTo(expected);
    }
}
