package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequest.HttpRequestBuilder;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RootControllerTest {

	@Test
	@DisplayName("root 경로로 접근시 hello word를 반환한다.")
	void handleTo() {
		final String plainRequest = String.join("\r\n",
			"GET / HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

		final HttpResponse httpResponse = new RootController().handleTo(request);

		final String expected = String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Type: text/html;charset=utf-8 ",
			"Content-Length: 12 ",
			"",
			"Hello world!");
		assertThat(httpResponse.buildResponse())
			.isEqualTo(expected);
	}

	@Nested
	@DisplayName("handle 되는 조건을 확인할 수 있다.")
	class IsSupported {

		@Test
		@DisplayName("/로 접근하면 true를 반환한다.")
		void success() {
			final String plainRequest = String.join("\r\n",
				"GET / HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");
			final HttpRequest request = HttpRequestBuilder.from(plainRequest)
				.build();

			final boolean supported = new RootController().isSupported(request);

			assertThat(supported)
				.isTrue();
		}

		@Test
		@DisplayName("/외 경로로 접근하면 true를 반환한다.")
		void fail() {
			final String plainRequest = String.join("\r\n",
				"GET /a HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");
			final HttpRequest request = HttpRequestBuilder.from(plainRequest)
				.build();

			final boolean supported = new RootController().isSupported(request);

			assertThat(supported)
				.isFalse();
		}
	}

}
