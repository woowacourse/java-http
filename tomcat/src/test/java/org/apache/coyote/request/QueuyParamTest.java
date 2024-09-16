package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.request.QueuyParam;
import org.junit.jupiter.api.Test;

class QueuyParamTest {

    @Test
    void hasQueryParam() {
        QueuyParam queuyParam = new QueuyParam("GET /index.html?name=kirby&part=backend HTTP/1.1");
        assertThat(queuyParam.hasQueryParam()).isTrue();
    }

    @Test
    void getQueryParam() {
        QueuyParam queuyParam = new QueuyParam("GET /index.html?name=kirby&part=backend HTTP/1.1");
        assertThat(queuyParam.getQueryParam("name")).isEqualTo("kirby");
    }
}
