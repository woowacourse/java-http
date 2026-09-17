package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTargetTest {

    @Test
    void parseRequestTarget() {
        HttpRequestTarget target = new HttpRequestTarget("/login?account=gugu");

        assertThat(target.getPath()).isEqualTo("/login");
        assertThat(target.getParams("account")).isEqualTo("gugu");
    }

    @Test
    void requestTargetWithoutQueryString() {
        HttpRequestTarget target = new HttpRequestTarget("/index.html");

        assertThat(target.getPath()).isEqualTo("/index.html");
        assertThat(target.getParams("account")).isNull();
    }
}
