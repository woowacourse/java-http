package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseBodyTest {

    @DisplayName("값이 있으면 true를 반환한다.")
    @Test
    void hasBody() {
        HttpResponseBody httpResponseBody = new HttpResponseBody("안녕하세요");

        boolean exists = httpResponseBody.hasBody();

        assertThat(exists).isTrue();
    }

    @DisplayName("값이 없으면 false를 반환한다.")
    @Test
    void hasNotBody() {
        HttpResponseBody httpResponseBody = new HttpResponseBody("");

        boolean exists = httpResponseBody.hasBody();

        assertThat(exists).isFalse();
    }


    @DisplayName("새로운 바디 값으로 변경한다.")
    @Test
    void update() {
        HttpResponseBody httpResponseBody = new HttpResponseBody("안녕하세요");
        String newBodyValue = "반가워요";
        httpResponseBody.update(newBodyValue);

        String value = httpResponseBody.getValue();

        assertThat(value).isEqualTo(newBodyValue);
    }
}
