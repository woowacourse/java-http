package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseBodyTest {
    @Test
    void body를_추가한다() {
        // given
        ResponseBody responseBody = new ResponseBody();

        // when
        responseBody.setBody("/index.html");

        // then
        assertThat(responseBody.getContent()).isNotEmpty();
    }
}
