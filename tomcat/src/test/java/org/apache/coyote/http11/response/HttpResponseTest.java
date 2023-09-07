package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;
import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

	@Test
	@DisplayName("Location을 넘기면 redirect 하는 응답을 반환해준다.")
	void redirect() {
		final String location = "/401.html";
		final HttpResponse actual = new HttpResponse();

		actual.redirect(location);

		final HttpHeaders httpHeaders = HttpHeaders.of("", MimeType.HTML);
		httpHeaders.addLocation(location);
		final HttpResponse expected = new HttpResponse(
			TEMPORARILY_MOVED_302,
			"",
			httpHeaders
		);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("set-cookie를 추가할 수 있다.")
	void addSetCookie() {
		final String setCookieValue = "set-cookie Value";
		final HttpResponse actual = new HttpResponse();
		actual.setResponse(OK_200,"", MimeType.HTML);

		actual.addSetCookie(setCookieValue);

		assertThat(actual.buildResponse())
			.contains(String.format("%s: %s", SET_COOKIE.getValue(), setCookieValue));
	}

	@Test
	@DisplayName("HTTP response에서 응답에 대한 값들을 추가할 수 있다.")
	void setBody() {
		final String body = "Hello world!";
		final HttpResponse httpResponse = new HttpResponse();

		httpResponse.setResponse(HttpStatusCode.OK_200, body, MimeType.HTML);

		assertThat(httpResponse.buildResponse())
			.contains(body, String.format("%s: %d", CONTENT_LENGTH.getValue(), 12), OK_200.buildResponse());
	}
}
