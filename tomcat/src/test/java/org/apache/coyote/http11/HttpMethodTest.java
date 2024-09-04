package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 메서드 테스트")
class HttpMethodTest {

    @DisplayName("String에 맞는 HTTP 메서드를 반환한다.")
    @Test
    void findMethod() {
        // given
        String get = "GET";
        String post = "Post";

        // when
        HttpMethod getFound = HttpMethod.from(get);
        HttpMethod postFound = HttpMethod.from(post);

        // then
        assertAll(
                () -> assertThat(getFound).isEqualTo(HttpMethod.GET),
                () -> assertThat(postFound).isEqualTo(HttpMethod.POST)
        );
    }
}
