package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

	@DisplayName("String을 파싱하여 RequestLine을 생성한다.")
	@Test
	void from() {
		// given 
		String requestLine = "GET / HTTP/1.1";
		// when 
		RequestLine result = RequestLine.from(requestLine);
		//then
		assertThat(result.getMethod()).isEqualTo(HttpMethod.GET);
		assertThat(result.getPath()).isEqualTo("/");
		assertThat(result.getVersion()).isEqualTo("HTTP/1.1");
	}

	@DisplayName("파싱할 String 이 비어있으면 예외를 발생한다.")
	@Test
	void from_withInvalidLine() {
		// given
		String requestLine = "";
		// when & then
		assertThatThrownBy(() -> RequestLine.from(requestLine))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Request line is empty");
	}
}
