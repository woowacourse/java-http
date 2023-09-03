package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseLineTest {

    @Test
    @DisplayName("ResponseLine을 생성한다.")
    void createResponseLine() {
        //given
        final HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        final StatusCode statusCode = StatusCode.OK;

        //when
        final ResponseLine responseLine = new ResponseLine(httpVersion, statusCode);

        //then
        final String expected = "HTTP/1.1 200 OK ";
        assertThat(responseLine.parse()).isEqualTo(expected);
    }
}
