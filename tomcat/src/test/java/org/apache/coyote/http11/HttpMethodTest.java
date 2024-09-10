package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("String형 메서드로 HttpMethod를 찾는다.")
    @Test
    void getHttpMethod() {
        // given
        String method = "POST";

        // when
        HttpMethod result = HttpMethod.of(method);

        // then
        assertThat(result).isEqualTo(HttpMethod.POST);
    }

    @DisplayName("지원하지 않는 String형 메서드로 HttpMethod를 찾을 경우, NONE을 반환한다.")
    @Test
    void getNoneHttpMethod() {
        // given
        String method = "WRONG";

        // when
        HttpMethod result = HttpMethod.of(method);

        // then
        assertThat(result).isEqualTo(HttpMethod.NONE);
    }

    @DisplayName("POST 메서드가 맞으면 참을 반환한다.")
    @Test
    void isPost() {
        // given
        HttpMethod method = HttpMethod.POST;

        // when & then
        assertThat(method.isPost()).isTrue();
    }

    @DisplayName("GET 메서드가 아니면 거짓을 반환한다.")
    @Test
    void isNotGet() {
        // given
        HttpMethod method = HttpMethod.POST;

        // when & then
        assertThat(method.isGet()).isFalse();
    }
}
