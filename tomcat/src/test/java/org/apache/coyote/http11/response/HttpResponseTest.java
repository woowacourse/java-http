package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpResponse.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("response message 정상 응답")
    @Test
    void getResponseMessage() {
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        final HttpResponse httpResponse = new Builder()
                .status(HttpStatus.OK)
                .contentType("text/html")
                .responseBody("Hello world!")
                .build();

        final String responseMessage = httpResponse.toResponseMessage();
        assertThat(responseMessage).isEqualTo(expected);
    }

}
