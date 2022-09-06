package org.apache.coyote.http11;

import static org.apache.coyote.http11.StatusCode.OK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.ResourcePathNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11ResponseTest {

    @DisplayName("존재하지 않는 경로를 요청하면 예외를 반환한다.")
    @Test
    void getResponse_ResourcePathNotFoundException() {
        assertThatThrownBy(() -> Http11Response.from(OK, "야호"))
                .isInstanceOf(ResourcePathNotFoundException.class);
    }
}
