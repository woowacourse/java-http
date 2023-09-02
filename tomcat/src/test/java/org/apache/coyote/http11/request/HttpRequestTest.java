package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

	@Test
	@DisplayName("http 요청 문자열을 객체로 생성")
	void createHttpRequest() {
		//given
		final String httpRequest = String.join("\r\n",
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final Map<String, String> expectedHeader = Map.of(
			"Host", "localhost:8080",
			"Connection", "keep-alive"
		);

		//when
		final HttpRequest actual = HttpRequest.from(httpRequest);

		//then
		assertAll(
			() -> assertThat(actual.getHttpMethod())
				.isEqualTo(HttpMethod.GET),
			() -> assertThat(actual.getEndPoint())
				.isEqualTo("/index.html"),
			() -> assertThat(actual.getHeaders().getHeaders())
				.containsExactlyInAnyOrderEntriesOf(expectedHeader)
		);
	}
}
