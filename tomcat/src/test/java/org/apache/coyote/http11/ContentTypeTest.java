package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

	@DisplayName("주어진 extension 에 해당하는 ContentType을 반환한다.")
	@Test
	void getExtension() {
		// given
		String extension = "js";

		// when
		String contentType = ContentType.getByExtension(extension);

		// then
		assertThat(contentType).isEqualTo("application/javascript");
	}

	@DisplayName("지원하지 않는 extension 으로 요청하는 경우 예외를 발생한다.")
	@Test
	void getExtension_withInvalidExtension() {
		// given
		String extension = "jss";

		// when & then
		assertThatThrownBy(() -> ContentType.getByExtension(extension))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("cannot find content-type by extension : " + extension);
	}
}
