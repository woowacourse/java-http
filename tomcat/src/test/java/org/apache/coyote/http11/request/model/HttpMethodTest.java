package org.apache.coyote.http11.request.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.http.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("GET 메서드를 조회한다.")
    void get() {
        HttpMethod httpMethod = HttpMethod.of("GET");

        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("GET 메서드인지 확인한다.")
    void isGet() {
        HttpMethod httpMethod = HttpMethod.GET;

        assertThat(httpMethod.isGet()).isTrue();
    }

    @Test
    @DisplayName("POST 메서드인지 확인한다.")
    void post() {
        HttpMethod httpMethod = HttpMethod.of("POST");

        assertThat(httpMethod).isEqualTo(HttpMethod.POST);
    }
}
