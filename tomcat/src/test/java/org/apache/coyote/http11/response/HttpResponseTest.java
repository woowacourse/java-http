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

		final HttpResponse actual = HttpResponse.redirect(location);

		final HttpHeaders httpHeaders = HttpHeaders.of("", MimeType.HTML);
		httpHeaders.put(LOCATION.getValue(), location);
		final HttpResponse expected = new HttpResponse(
			TEMPORARILY_MOVED_302,
			"",
			httpHeaders
		);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}
}
