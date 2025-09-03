package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestPathTest {

    @Test
    void 이름으로_생성한다() {
        // given
        String path = "/path";
        RequestPath requestPath = RequestPath.from(path);

        // when
        String actual = requestPath.getPath();

        // then
        assertThat(actual).isEqualTo(path);
    }
}