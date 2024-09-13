package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

	@DisplayName("헤더 이름 String으로 HttpHeader 반환")
	@Test
	void from() {
		// given
		String headerName = "Connection";

		// when
		HttpHeader httpHeader = HttpHeader.from(headerName);

		// then
		assertThat(httpHeader).isEqualTo(HttpHeader.CONNECTION);
	}

	@DisplayName("지원하지 않는 헤더 이름 String으로 조회시 예외발생")
	@Test
	void from_withInvalidHeaderName() {
		// given
		String headerName = "";

		// when & then
		assertThatThrownBy(() -> HttpHeader.from(headerName))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Unknown header: " + headerName);
	}

}
