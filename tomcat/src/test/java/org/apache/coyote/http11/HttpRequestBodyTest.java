package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestBodyTest {

	@DisplayName("body 문자열을 파싱하여 HttpRequestBody를 생성한다.")
	@Test
	void from() {
		// given
		String body = "account=gugu&password=password";

		// when
		HttpRequestBody httpRequestBody = HttpRequestBody.from(body);

		// then
		assertThat(httpRequestBody.get("account")).isEqualTo("gugu");
		assertThat(httpRequestBody.get("password")).isEqualTo("password");
	}

	@DisplayName("비어있는 body String 으로는 생성할 수 없다.")
	@Test
	void from_withEmptyBody() {
		// given
		String body = "";

		// when & then
		assertThatThrownBy(() -> HttpRequestBody.from(body))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Request body is Empty");
	}

	@DisplayName("빈 HttpRequestBody를 반환한다.")
	@Test
	void empty() {
		// when
		HttpRequestBody emptyBody = HttpRequestBody.empty();

		// then
		assertThatThrownBy(() -> emptyBody.get("something")).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Request body is not initialized");
	}
}
