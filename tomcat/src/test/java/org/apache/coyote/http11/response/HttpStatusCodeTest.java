package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpStatusCodeTest {

    @Test
    void 상태코드를_반환한다() {
        // given
        HttpStatusCode httpStatusCode = HttpStatusCode.OK;

        // when
        int code = httpStatusCode.getCode();

        // then
        assertThat(code).isEqualTo(200);
    }
}
