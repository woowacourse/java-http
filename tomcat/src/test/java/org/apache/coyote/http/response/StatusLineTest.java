package org.apache.coyote.http.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusLineTest {

    @Test
    @DisplayName("RedirectLocation 이 필요한 헤더인 경우 참을 반환한다.")
    void needRedirectLocation() {
        StatusLine found = new StatusLine(HttpStatus.FOUND);
        StatusLine notFound = new StatusLine(HttpStatus.NOT_FOUND);

        assertThat(found.needRedirectLocation()).isTrue();
        assertThat(notFound.needRedirectLocation()).isFalse();
    }

}
