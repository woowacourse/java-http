package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestUriTest {

    @Test
    void 이름으로_생성한다() {
        // given
        String path = "/path";
        RequestUri requestUri = RequestUri.from(path);

        // when
        String actual = requestUri.getPath();

        // then
        assertThat(actual).isEqualTo(path);
    }
}