package org.apache.coyote;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpProtocolVersionTest {

	@Nested
	class HttpProtocolVersion_확인 {

		@Test
		void 지원하는_프로토콜일시_반환() {
			final var version = HttpProtocolVersion.from("HTTP/1.1");
			assertThat(version).isEqualTo(HttpProtocolVersion.HTTP11);
		}

		@Test
		void 지원하지_않는_프로토콜이면_예외() {
			assertThatThrownBy(() -> HttpProtocolVersion.from("nono"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("지원하지 않는 HTTP 프로토콜 버전입니다.");
		}
	}
}
