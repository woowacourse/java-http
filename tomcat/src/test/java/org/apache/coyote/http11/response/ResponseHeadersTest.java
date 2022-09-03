package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @Test
    void createResponseHeaders() {
        // given
        String bodyString = "hello world";
        // when
        ResponseHeaders headers = ResponseHeaders.from(bodyString);
        // then
        assertThat(headers).isNotNull();
    }

    @Test
    void getAsString() {
        // given
        String bodyString = "hello world";
        ResponseHeaders headers = ResponseHeaders.from(bodyString);

        // when
        String headersString = headers.getAsString();

        // then
        assertThat(headersString).isEqualTo("Content-Length: 11");
    }
}
