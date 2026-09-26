package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestTargetTest {

    @Test
    void 요청_대상을_경로와_Query_Parameter로_해석한다() {
        RequestTarget target = new RequestTarget("/login?account=gugu&password=password");

        assertThat(target.getPath()).isEqualTo("/login");
        assertThat(target.hasPath("/login")).isTrue();
        assertThat(target.findQueryParameter("account")).contains("gugu");
        assertThat(target.findQueryParameter("password")).contains("password");
        assertThat(target.findQueryParameter("unknown")).isEmpty();
    }

    @Test
    void Query_Parameter가_없는_요청_대상을_해석한다() {
        RequestTarget target = new RequestTarget("/login");

        assertThat(target.getPath()).isEqualTo("/login");
        assertThat(target.findQueryParameter("account")).isEmpty();
    }

    @Test
    void 요청_경로의_확장자를_반환한다() {
        assertThat(new RequestTarget("/css/styles.css").getExtension())
                .isEqualTo("css");

        assertThat(new RequestTarget("/login").getExtension())
                .isEmpty();
    }
}
