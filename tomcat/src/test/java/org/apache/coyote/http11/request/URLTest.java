package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.exception.InvalidHttpRequestException;

class URLTest {

	@DisplayName("url 이 공백이면 예외를 발생시킨다.")
	@Test
	void url_empty() {
		// given
		String url = "";
		// when & then
		assertThatThrownBy(() -> URL.from(url))
			.isInstanceOf(InvalidHttpRequestException.class);
	}
}
