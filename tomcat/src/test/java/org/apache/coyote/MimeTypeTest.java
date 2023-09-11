package org.apache.coyote;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MimeTypeTest {

	@Nested
	class path의_MimeType확인 {

		@Test
		void 파일_확장자로_확인() {
			final var mimeType = MimeType.fromPath("hi.html");
			assertThat(mimeType).isEqualTo(MimeType.HTML);
		}

		@Test
		void 지원하지_않는_확장자이면_예외() {
			assertThatThrownBy(() -> MimeType.fromPath("hi.html2"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("지원하지 않는 파일 확장자입니다.");
		}

		@Test
		void 파일_경로가_null이면_예외() {
			assertThatThrownBy(() -> MimeType.fromPath(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("파일 경로는 null일 수 없습니다.");
		}

		@Test
		void 파일_확장자가_없으면_예외() {
			assertThatThrownBy(() -> MimeType.fromPath("hi"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("올바르지 않은 파일 경로입니다.");
		}
	}

	@Nested
	class MimeType_문자열반환 {

		@Test
		void 문자이면_charset을_포함해_반환() {
			final var mimeType = MimeType.HTML;

			assertThat(mimeType.getValue()).isEqualTo("text/html;charset=utf-8");
		}

		@Test
		void 문자가_아니면_그냥_반환() {
			final var mimeType = MimeType.SVG;

			assertThat(mimeType.getValue()).isEqualTo("image/svg+xml");
		}
	}
}
