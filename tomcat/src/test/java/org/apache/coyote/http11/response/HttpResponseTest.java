package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void createResponseWithStatusAndBody() {
        // given
        String bodyString = "hello world";
        HttpStatus status = HttpStatus.OK;

        // when
        HttpResponse response = HttpResponse.from(status, bodyString);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    void getAsString() {
        // given
        String bodyString = "hello world";
        HttpStatus status = HttpStatus.OK;
        HttpResponse response = HttpResponse.from(status, bodyString);

        // when
        String responseString = response.getAsString();

        // then
        assertThat(responseString).isEqualTo(
                String.join("\n",
                        "HTTP/1.1 200 OK",
                        "Content-Length: 11",
                        "",
                        "hello world")
        );
    }
}
