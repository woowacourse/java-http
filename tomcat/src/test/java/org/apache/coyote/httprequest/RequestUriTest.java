package org.apache.coyote.httprequest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
class RequestUriTest {

    @Test
    void 생성에_성공한다() {
        // given
        final String pathInput = "/search";
        final String queryStringInput = "name=bebe&age=19";
        final String requestUriInput = String.join("?", pathInput, queryStringInput);

        // when
        final RequestUri requestUri = RequestUri.from(requestUriInput);

        // then
        assertSoftly(softly -> {
            assertThat(requestUri.getPath()).isEqualTo(pathInput);
            assertThat(requestUri.getQueryString()).isEqualTo(QueryString.from(queryStringInput));
        });
    }

    @Test
    void 쿼리_스트링이_없는_생성에_성공한다() {
        // given
        final String pathInput = "/search";

        // when
        final RequestUri requestUri = RequestUri.from(pathInput);

        // then
        assertSoftly(softly -> {
            assertThat(requestUri.getPath()).isEqualTo(pathInput);
            assertThat(requestUri.getQueryString()).isEqualTo(QueryString.empty());
        });
    }
}
