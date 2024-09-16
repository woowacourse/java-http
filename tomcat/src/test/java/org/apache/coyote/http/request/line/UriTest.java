package org.apache.coyote.http.request.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UriTest {

    @Test
    @DisplayName("isHome 메서드는 path가 '/'일 때 true를 반환한다.")
    void isHomeTrue() {
        Uri uri = new Uri("/");
        assertThat(uri.isHome()).isTrue();
    }

    @Test
    @DisplayName("isHome 메서드는 path가 '/'가 아닐 때 false를 반환한다.")
    void isHomeFalse() {
        Uri uri = new Uri("/test");
        assertThat(uri.isHome()).isFalse();
    }
}
