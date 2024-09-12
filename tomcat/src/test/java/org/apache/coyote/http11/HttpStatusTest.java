package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpStatusTest {

	@DisplayName("주어진 int 코드로 HttpStatus 를 반환한다.")
	@Test
	void from() {
		// given
		int code = 201;

		// when
		HttpStatus httpStatus = HttpStatus.from(code);

		// then
		assertThat(httpStatus.getCode()).isEqualTo(201);
		assertThat(httpStatus.getMessage()).isEqualTo("Created");
	}

	@DisplayName("존재하지 않는 코드로 요청시 예외를 발생한다.")
	@Test
	void from_withInvalidCode() {
		// given
		int code = 211;

		// when & then
		assertThatThrownBy(() -> HttpStatus.from(code))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Unknown HTTP status code: " + code);
	}
}
