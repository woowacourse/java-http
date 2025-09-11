package com.techcourse.http.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.NotFoundException;
import com.techcourse.http.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("Get 메서드 찾기")
    @Test
    void fromTest1() {
        // given
        String method = "GET";

        // when
        HttpMethod httpMethod = HttpMethod.from(method);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("소문자로 작성된 메서드 찾기")
    @Test
    void fromTest2() {
        // given
        String method = "get";

        // when
        HttpMethod httpMethod = HttpMethod.from(method);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("대소문자 혼합으로 작성된 메서드 찾기")
    @Test
    void fromTest3() {
        // given
        String method = "Put";

        // when
        HttpMethod httpMethod = HttpMethod.from(method);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.PUT);
    }

    @DisplayName("존재하지 않는 메서드인 경우")
    @Test
    void fromTest4() {
        // given
        String method = "INVALID";

        // when & then
        assertThatThrownBy(() -> HttpMethod.from(method))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 http method 입니다.");
    }
}
