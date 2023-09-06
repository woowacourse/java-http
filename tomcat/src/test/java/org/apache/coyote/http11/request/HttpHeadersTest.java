package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.headers.MimeType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.apache.coyote.http11.headers.HttpCookie;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

	@Test
	@DisplayName("http 요청에서 header를 파싱한다.")
	void createHttpHeader() {
		//given
		final String httpRequest = String.join("\r\n",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Cookie: salty=fries",
			"",
			"");
		final HttpHeaders expected = new HttpHeaders(Map.of(
			"Host", "localhost:8080",
			"Connection", "keep-alive"
		), new HttpCookie(
			Map.of("salty", "fries")
		));
		//when
		final HttpHeaders actual = HttpHeaders.from(httpRequest);

		//then
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("jssession id가 헤더에 존재하는지 확인한다.")
	void isExistJSessionId() {
		//given
		final String httpRequest = String.join("\r\n",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Cookie: JSESSIONID=str",
			"",
			"");
		final HttpHeaders headers = HttpHeaders.from(httpRequest);

		assertThat(headers.isExistJSessionId())
			.isTrue();
	}

	@Test
	@DisplayName("JsessionId 값을 Optional로 반환해줄 수 있다.")
	void findJSessionId() {
		final String jSessionId = "str";
		final String httpRequest = String.join("\r\n",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Cookie: JSESSIONID=" + jSessionId,
			"",
			"");
		final HttpHeaders headers = HttpHeaders.from(httpRequest);

		assertThat(headers.findJSessionId())
			.contains(jSessionId);
	}

	@Test
	@DisplayName("body와 contentType으로, HttpHeaders를 반환할 수 있다.")
	void createByBodyAndContentType() {
		final String body = "hello";

		final HttpHeaders actual = HttpHeaders.of(body, HTML);

		final HttpHeaders expected = new HttpHeaders();
		expected.put(CONTENT_TYPE.getValue(), HTML.getValue());
		expected.put(CONTENT_LENGTH.getValue(), "5");

		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

}
