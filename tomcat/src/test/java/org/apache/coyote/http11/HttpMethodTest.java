package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

	@DisplayName("String으로 전달된 값을 HttpMethod로 치환한다.")
	@Test
	void from() {
		// given
		String method = "GET";

		// when
		HttpMethod actual = HttpMethod.from(method);

		// then
		assertThat(actual).isEqualTo(HttpMethod.GET);
	}

	@DisplayName("String으로 전달된 값을 HttpMethod로 치환한다.")
	@Test
	void from_withInvalidHttpMethodString() {
		// given
		String method = "GUY";

		// when & then
		assertThatThrownBy(() -> HttpMethod.from(method))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("invalid HTTP Method");
	}
}
