package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 상태 코드 테스트")
class HttpStatusCodeTest {

    @DisplayName("리다이렉션인지 검증한다.")
    @Test
    void isRedirect() {
        // given
        HttpStatusCode code1 = HttpStatusCode.CREATED;
        HttpStatusCode code2 = HttpStatusCode.FOUND;

        // when & then
        assertThat(code1.isRedirection()).isFalse();
        assertThat(code2.isRedirection()).isTrue();
    }
}
