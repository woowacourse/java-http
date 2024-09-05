package org.apache.coyote.request.uri;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class URITest {

    @Test
    void URI_객체를_생성한다() {
        // given
        String uri = "/user?team=ddangkong";

        // when
        URI actual = new URI(uri);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getPath()).isEqualTo("/user");
            softly.assertThat(actual.getQueryParamValue("team")).isEqualTo("ddangkong");
        });
    }
}
