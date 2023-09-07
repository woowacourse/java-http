package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequest.HttpRequestBuilder;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StaticResourceHandlerTest {

	private static final StaticResourceController HANDLER = new StaticResourceController();

	@Nested
	@DisplayName("handle 되는 조건을 확인할 수 있다.")
	class IsSupported {

		@Test
		@DisplayName("endpoint가 resources에 있는 파일인 경우 true를 반환한다.")
		void success() {
			final HttpRequest request = HttpRequestBuilder
				.from(String.join("\r\n",
					"GET /index.html HTTP/1.1 ",
					"Host: localhost:8080 "
				))
				.build();

			final boolean supported = HANDLER.isSupported(request);

			assertThat(supported)
				.isTrue();
		}

		@Test
		@DisplayName("endpoint가 resources에 없는 파일인 경우 false를 반환한다.")
		void fail() {
			final HttpRequest request = HttpRequestBuilder
				.from(String.join("\r\n",
					"GET /index1.html HTTP/1.1 ",
					"Host: localhost:8080 "
				))
				.build();

			final boolean supported = HANDLER.isSupported(request);

			assertThat(supported)
				.isFalse();
		}
	}

	@Test
	@DisplayName("요청에 해당하는 적절한 값을 반환한다,")
	void handleTo() throws IOException {
		final String plainRequest = String.join("\r\n",
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

		final HttpResponse httpResponse = HANDLER.handleTo(request);

		final URL resource = getClass().getClassLoader().getResource("static/index.html");
		final String expected = "HTTP/1.1 200 OK \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 5564 \r\n" +
			"\r\n" +
			new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

		assertThat(httpResponse.buildResponse())
			.isEqualTo(expected);
	}
}
