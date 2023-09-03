package org.apache.coyote.http11.handler.exception;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InternalServerErrorHandlerTest {

	@Test
	@DisplayName("302 상태코드와 500.html Location을 반환한다.")
	void handleTo() throws IOException {
		final InternalServerErrorHandler handler = new InternalServerErrorHandler();

		final HttpResponse actual = handler.handleTo();

		final String expected = "HTTP/1.1 302 Found \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 0 \r\n" +
			"Location: http://localhost:8080/500.html \r\n" +
			"\r\n";

		assertThat(actual.buildResponse())
			.isEqualTo(expected);
	}
}
