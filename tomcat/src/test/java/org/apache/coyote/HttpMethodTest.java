package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.coyote.header.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpMethodTest {

    @Test
    void 문자열로_HTTP_Method를_찾는다() {
        // given
        String requestMethod = "GET";

        // when
        HttpMethod result = HttpMethod.from(requestMethod);

        // then
        HttpMethod expect = HttpMethod.GET;
        assertThat(expect).isEqualTo(result);
    }

    @Test
    void 존재하지_않는_Method이면_예외가_발생한다() {
        // given
        String requestMethod = "GETPOST";

        // expect
        assertThrows(RuntimeException.class, () -> HttpMethod.from(requestMethod));
    }
}
