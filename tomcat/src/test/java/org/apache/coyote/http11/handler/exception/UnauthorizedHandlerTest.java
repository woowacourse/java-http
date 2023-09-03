package org.apache.coyote.http11.handler.exception;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

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
	@DisplayName("401 상태코드와 함께 401.html을 반환한다.")
	void handleTo() throws IOException {
		final HttpResponse actual = UNAUTHORIZED_HANDLER.handleTo();

		final URL resource = getClass().getClassLoader().getResource("static/401.html");
		final String expected = "HTTP/1.1 401 Unauthorized \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 2478 \r\n" +
			"\r\n" +
			new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

		assertThat(actual.buildResponse())
			.isEqualTo(expected);
	}
}
