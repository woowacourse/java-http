package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseBodyTest {

    @Test
    void ResponseBody를_조립한다() {
        // given
        ResponseBody responseBody = new ResponseBody("Hello, World!");

        // when
        StringBuilder builder = new StringBuilder();
        responseBody.assemble(builder);

        // then
        assertThat(builder.toString()).isEqualTo("Hello, World!");
    }
}
