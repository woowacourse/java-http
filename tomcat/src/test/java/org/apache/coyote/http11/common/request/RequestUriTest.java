package org.apache.coyote.http11.common.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestUriTest {

    @Test
    void 쿼리파라미터가_있는_경우_물음표_전까지만() {
        // given
        String input = "/login?account=gugu&password=pw";

        // when
        RequestUri requestUri = RequestUri.create(input);

        // then
        assertThat(requestUri.getValue()).isEqualTo("/login");
    }

    @Test
    void 쿼리파라미터가_없는_경우_전부() {
        // given
        String input = "/login";

        // when
        RequestUri requestUri = RequestUri.create(input);

        // then
        assertThat(requestUri.getValue()).isEqualTo("/login");
    }

    @Test
    void 확장자를_반환할_수_있다() {
        // given
        String input = "/index.html";
        RequestUri requestUri = RequestUri.create(input);

        // when
        String extension = requestUri.getExtension();

        // then
        assertThat(extension).isEqualTo(".html");
    }
}
