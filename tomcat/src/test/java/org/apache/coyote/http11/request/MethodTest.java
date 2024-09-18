package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MethodTest {
	@DisplayName("요청 헤더의 첫번째 라인에서 Method를 성공적으로 추출한다.")
	@Test
	void parseRequestToMethod() {
		String requestLine = "GET / HTTP/1.1";

		Method method = Method.parseRequestToMethod(requestLine);

		Assertions.assertThat(method).isEqualTo(new Method("GET"));
	}
}