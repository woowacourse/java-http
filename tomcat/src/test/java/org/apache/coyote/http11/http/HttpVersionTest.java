package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotFoundException;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @Test
    void from() {
        HttpVersion httpVersion = HttpVersion.from("HTTP/1.1");

        assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    void fail() {
        assertThatThrownBy(() -> HttpVersion.from("HTTP 1.1"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("HTTP-Version not found");
    }
}
