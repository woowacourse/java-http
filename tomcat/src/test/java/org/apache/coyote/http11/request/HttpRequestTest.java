package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.apache.coyote.http11.headers.HttpCookie;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest.HttpRequestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

	@Test
	@DisplayName("http 요청 문자열을 객체로 생성")
	void createHttpRequest() {
		//given
		final String reqeustBody = "body";
		final String httpRequest = String.join("\r\n",
			"GET /index.html?user=hong HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"");
		final HttpHeaders expectedHeader = new HttpHeaders(Map.of(
			"Host", "localhost:8080",
			"Connection", "keep-alive"
		), new HttpCookie());
		final QueryParam queryParam = new QueryParam(Map.of(
			"user", "hong"
		));

		//when
		final HttpRequest actual = HttpRequestBuilder.from(httpRequest)
			.body(reqeustBody)
			.build();

		//then
		assertAll(
			() -> assertThat(actual.getHttpMethod())
				.isEqualTo(HttpMethod.GET),
			() -> assertThat(actual.getEndPoint())
				.isEqualTo("/index.html"),
			() -> assertThat(actual.getHeaders())
				.usingRecursiveComparison()
				.isEqualTo(expectedHeader),
			() -> assertThat(actual.getQueryParam())
				.usingRecursiveComparison()
				.isEqualTo(queryParam),
			() -> assertThat(actual.getBody().get())
				.isEqualTo("body")
		);
	}
}
