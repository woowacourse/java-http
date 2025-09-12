package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("대소문자 무시하고 HttpMethod 파싱")
    void from_caseInsensitive() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThat(HttpMethod.from("get")).isEqualTo(HttpMethod.GET);
            softly.assertThat(HttpMethod.from("PoSt")).isEqualTo(HttpMethod.POST);
        });
    }

    @Test
    @DisplayName("잘못된 메서드 또는 null은 예외")
    void from_invalidOrNull_throws() {
        assertThatThrownBy(() -> HttpMethod.from("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> HttpMethod.from(null))
                .isInstanceOf(NullPointerException.class);
    }
}
