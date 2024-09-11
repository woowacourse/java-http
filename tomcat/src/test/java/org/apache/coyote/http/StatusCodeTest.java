package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusCodeTest {

    @Test
    void StatusCode를_조립한다() {
        // given
        StatusCode statusCode = StatusCode.OK;

        // when
        StringBuilder builder = new StringBuilder();
        statusCode.assemble(builder);

        // then
        assertThat(builder.toString()).isEqualTo("200 OK \r\n");
    }
}
