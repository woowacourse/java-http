package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestTargetTest {

    @Test
    void 유효한_요청_대상을_요청_경로와_Query_Parameter로_해석한다() {
        // when
        final var requestTarget = new RequestTarget("/login?account=gugu&password=password");

        // then
        assertThat(requestTarget.getPath()).isEqualTo("/login");
        assertThat(requestTarget.findQueryParameter("account")).contains("gugu");
        assertThat(requestTarget.findQueryParameter("password")).contains("password");
    }

    @Test
    void 요청_경로가_주어진_경로와_일치하는지_판단한다() {
        // given
        final var requestTarget = new RequestTarget("/login");

        // when & then
        assertThat(requestTarget.hasPath("/login")).isTrue();
        assertThat(requestTarget.hasPath("/index.html")).isFalse();
    }

    @Test
    void Query_Parameter가_존재하는지_판단한다() {
        // given
        final var requestTarget = new RequestTarget("/login?account=gugu");

        // when & then
        assertThat(requestTarget.hasQueryParameters()).isTrue();
    }

    @Test
    void 이름으로_Query_Parameter를_찾는다() {
        // given
        final var requestTarget = new RequestTarget("/login?account=gugu");

        // when & then
        assertThat(requestTarget.findQueryParameter("account")).contains("gugu");
        assertThat(requestTarget.findQueryParameter("password")).isEmpty();
    }

    @Test
    void 요청_경로의_확장자를_반환한다() {
        // given
        final var requestTarget = new RequestTarget("/css/styles.css");

        // when
        final String extension = requestTarget.getExtension();

        // then
        assertThat(extension).isEqualTo("css");
    }
}
