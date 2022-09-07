package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StartLineTest {

    @Test
    void request_path를_확인할_수_있다() {
        // given
        String rawStartLine = "GET /eden HTTP/1.1";

        // when
        StartLine startLine = new StartLine(rawStartLine);

        // then
        assertThat(startLine.checkRequest("/eden")).isTrue();
    }

}
