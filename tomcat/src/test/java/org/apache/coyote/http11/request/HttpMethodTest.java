package org.apache.coyote.http11.request;

import org.apache.coyote.http11.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {
    @Test
    void from() {
        assertThat(HttpMethod.from("GET")).isEqualTo(HttpMethod.GET);
        assertThat(HttpMethod.from("POST")).isEqualTo(HttpMethod.POST);
    }

    @Test
    void unknownMethod() {
        assertThatThrownBy(() -> HttpMethod.from("ABC"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 메서드입니다: ABC");
    }

    @Test
    void methodIsCaseSensitive() {
        assertThatThrownBy(() -> HttpMethod.from("get"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 메서드입니다: get");
    }
}
