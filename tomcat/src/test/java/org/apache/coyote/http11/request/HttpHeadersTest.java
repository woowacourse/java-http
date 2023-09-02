package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

	@Test
	@DisplayName("http 요청에서 header를 파싱한다.")
	void createHttpHeader() {
		//given
		final String httpRequest = String.join("\r\n",
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final HttpHeaders expected = new HttpHeaders(Map.of(
			"Host", "localhost:8080",
			"Connection", "keep-alive"
		));
		//when
		final HttpHeaders actual = HttpHeaders.from(httpRequest);

		//then
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}
}
