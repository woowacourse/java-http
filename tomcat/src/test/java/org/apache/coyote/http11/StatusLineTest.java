package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusLineTest {

	@DisplayName("주어진 HttpStatus 로 StatusLine을 반환한다.")
	@Test
	void from() {
		// given
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

		// when
		StatusLine statusLine = StatusLine.from(httpStatus);

		//then
		assertThat(statusLine.getLine()).isEqualTo("HTTP/1.1 400 Bad Request");
	}

}
