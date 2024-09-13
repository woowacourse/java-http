package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class QueuryParamTest {

    @Test
    void hasQueryParam() {
        QueuryParam queuryParam = new QueuryParam("GET /index.html?name=kirby&part=backend HTTP/1.1");
        assertThat(queuryParam.hasQueryParam()).isTrue();
    }

    @Test
    void getQueryParam() {
        QueuryParam queuryParam = new QueuryParam("GET /index.html?name=kirby&part=backend HTTP/1.1");
        assertThat(queuryParam.getQueryParam("name")).isEqualTo("kirby");
    }
}
