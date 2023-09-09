package org.apache.coyote.http11;

import org.apache.coyote.http11.request.RequestURI;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestURITest {

    @Test
    void QueryString이_존재하면_true를_반환한다() {
        // given
        String uri = "/index.html";

        // when
        RequestURI requestURI = RequestURI.from(uri);

        // then
        assertThat(requestURI.hasQueryString()).isFalse();
    }

    @Test
    void QueryString이_없으면_false를_반환한다() {
        // given
        String uri = "/login?user=gugu&password=password";

        // when
        RequestURI requestURI = RequestURI.from(uri);

        // then
        assertThat(requestURI.hasQueryString()).isTrue();
    }


    @Test
    void uri에서_자원을_찾을_수_있다() {
        // given
        String uri = "/login?account=gugu&password=password";

        // when
        RequestURI requestURI = RequestURI.from(uri);

        // then
        assertThat(requestURI.getResourcePath()).isEqualTo("/login.html");
    }

}
