package org.apache.coyote;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CookieTest {

	@Nested
	class 세션_쿠키인지_획인 {

		@Test
		void 세션_쿠키이면_참() {
			final var cookie = new Cookie("JSESSIONID", "abcdefg");
			assertThat(cookie.isSession()).isTrue();
		}

		@Test
		void 세션_쿠키가_아니면_거짓() {
			final var cookie = new Cookie("COOKIE", "abcdefg");
			assertThat(cookie.isSession()).isFalse();
		}
	}
}
