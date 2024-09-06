package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseLineTest {

    @Test
    void ResponseLine을_조립한다() {
        // given
        ResponseLine responseLine = ResponseLine.create();
        responseLine.setStatusCode(StatusCode.OK);

        // when
        StringBuilder builder = new StringBuilder();
        responseLine.assemble(builder);

        // then
        assertThat(builder.toString()).isEqualTo("HTTP/1.1 200 OK \r\n");
    }
}
