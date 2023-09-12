package org.apache.coyote;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpMethodTest {

	@Nested
	class HttpMethod_확인 {

		@Test
		void 해당_문자열의_HttpMethod_반환() {
			final var httpMethod = HttpMethod.from("GET");
			assertThat(httpMethod).isEqualTo(HttpMethod.GET);
		}

		@Test
		void 소문자로_들어오면_대문자로_변환해_확인() {
			final var httpMethod = HttpMethod.from("get");
			assertThat(httpMethod).isEqualTo(HttpMethod.GET);
		}

		@Test
		void 일치하는_HttpMethod가_없으면_예외() {
			assertThatThrownBy(() -> HttpMethod.from("nono"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("지원하지 않는 HTTP 메서드입니다.");
		}
	}
}
