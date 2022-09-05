package org.apache.coyote.http11.httpmessage.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpStatusTest {

    @Test
    void 상태가_Found인지_확인한다() {
        // given
        HttpStatus status = HttpStatus.FOUND;

        // when
        boolean result = status.isFound();

        // then
        assertThat(result).isTrue();
    }


    @Test
    void 상태가_Found가_아닌지_확인한다() {
        // given
        HttpStatus status = HttpStatus.OK;

        // when
        boolean result = status.isFound();

        // then
        assertThat(result).isFalse();
    }
}
