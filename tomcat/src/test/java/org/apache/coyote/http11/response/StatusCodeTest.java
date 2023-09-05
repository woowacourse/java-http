package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusCodeTest {

    @Test
    @DisplayName("statusCode를 파싱한다.")
    void parse() {
        //given
        final StatusCode statusCode = StatusCode.OK;

        //when
        final String status = statusCode.getStatus();

        //then
        assertThat(status).isEqualTo("200 OK");
    }

}
