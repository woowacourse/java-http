package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CookieTest {

    @Test
    void 키값을_통해_쿠키가_있는지_확인할_수_있다() {
        // given
        Cookie cookie = new Cookie(Map.of("key", "value"));

        // when, then
        assertThat(cookie.hasKey("key")).isTrue();
    }

    @Test
    void 키값을_통해_값을_가져올_수_있다() {
        // given
        Cookie cookie = new Cookie(Map.of("key", "value"));

        // when, then
        assertThat(cookie.getValue("key")).isEqualTo("value");
    }
}
