package org.apache.coyote.http11.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTargetTest {
    @Test
    void parseRequestTarget() {
        HttpRequestTarget target = HttpRequestTarget.from("/login?account=gugu");

        assertThat(target.getPath()).isEqualTo("/login");
        assertThat(target.getParams("account")).isEqualTo("gugu");
    }

    @Test
    void requestTargetWithoutQueryString() {
        HttpRequestTarget target = HttpRequestTarget.from("/index.html");

        assertThat(target.getPath()).isEqualTo("/index.html");
        assertThat(target.getParams("account")).isNull();
    }
}
