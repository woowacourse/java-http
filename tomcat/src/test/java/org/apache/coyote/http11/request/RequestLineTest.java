package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

	@Test
	@DisplayName("한줄 요청이 들어올 떄 RequestLine 객체를 만들 수있다.")
	void createRequestLine() {
		final String requestLine = "GET /uri?id=hong-sile HTTP/1.1";

		final RequestLine actual = RequestLine.from(requestLine);

		final RequestLine expected = new RequestLine(
			HttpMethod.GET,
			"/uri",
			new QueryParam(Map.of("id", "hong-sile")),
			HttpVersion.HTTP_1_1
		);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

}
