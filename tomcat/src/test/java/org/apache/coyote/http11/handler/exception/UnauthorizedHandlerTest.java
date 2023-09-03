package org.apache.coyote.http11.handler.exception;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UnauthorizedHandlerTest {

	private static final UnauthorizedHandler UNAUTHORIZED_HANDLER = new UnauthorizedHandler();

	@Nested
	@DisplayName("UnauthorizedException이 들어오면 true를 반환한다.")
	class isSupported {

		@Test
		@DisplayName("UnauthroizedException이 들어온 경우")
		void trueCase() {
			final Exception exception = new UnauthorizedException();

			final boolean supported = UNAUTHORIZED_HANDLER.isSupported(exception);

			assertThat(supported)
				.isTrue();
		}

		@Test
		@DisplayName("UnauthroizedException외 다른 Exception이 들어온 경우")
		void falseCase() {
			final Exception exception = new RuntimeException();

			final boolean supported = UNAUTHORIZED_HANDLER.isSupported(exception);

			assertThat(supported)
				.isFalse();
		}
	}

	@Test
	@DisplayName("302 상태코드와 401.html Location을 반환한다.")
	void handleTo() throws IOException {
		final HttpResponse actual = UNAUTHORIZED_HANDLER.handleTo();

		final String expected = "HTTP/1.1 302 Found \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 0 \r\n" +
			"Location: http://localhost:8080/401.html \r\n" +
			"\r\n";

		assertThat(actual.buildResponse())
			.isEqualTo(expected);
	}
}
