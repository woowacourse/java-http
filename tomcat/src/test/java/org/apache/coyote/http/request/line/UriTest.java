package org.apache.coyote.http.request.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Uri 테스트")
class UriTest {

    @Test
    @DisplayName("isHome 메서드는 path가 '/'일 때 true를 반환한다.")
    void isHomeTrue() {
        // given
        Uri uri = new Uri("/");

        // when & then
        assertThat(uri.isHome()).isTrue();
    }

    @Test
    @DisplayName("isHome 메서드는 path가 '/'가 아닐 때 false를 반환한다.")
    void isHomeFalse() {
        // given
        Uri uri = new Uri("/test");

        // when & then
        assertThat(uri.isHome()).isFalse();
    }
}
