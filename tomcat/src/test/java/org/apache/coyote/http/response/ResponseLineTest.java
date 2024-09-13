package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.apache.coyote.http.StatusCode;

class ResponseLineTest {

    @Test
    void ResponseLine을_조립한다() {
        // given
        ResponseLine responseLine = new ResponseLine();
        responseLine.setStatusCode(StatusCode.OK);

        // when
        StringBuilder builder = new StringBuilder();
        responseLine.assemble(builder);

        // then
        assertThat(builder.toString()).isEqualTo("HTTP/1.1 200 OK \r\n");
    }
}
