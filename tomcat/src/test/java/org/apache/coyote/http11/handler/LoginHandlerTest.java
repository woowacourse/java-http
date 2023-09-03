package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

	private static final LoginHandler HANDLER = new LoginHandler();

	@Test
	@DisplayName("endpoint가 login이면 true를 반환한다.")
	void isSupported() {
		final String plainRequest = String.join("\r\n",
			"GET /login?account=hong&password=password HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final HttpRequest request = HttpRequest.from(plainRequest);

		final boolean supported = HANDLER.isSupported(request);

		assertThat(supported)
			.isTrue();
	}

	@Nested
	@DisplayName("/login endpoint로 접근 시 처리")
	class HandleTo {

		@Test
		@DisplayName("login.html 반환하고, 멤버 조회 후 로깅한다.")
		void handleTo() throws IOException {
			final String plainRequest = String.join("\r\n",
				"GET /login?account=gugu&password=password HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");
			final String file = "/login.html";
			final HttpRequest request = HttpRequest.from(plainRequest);

			final HttpResponse actual = HANDLER.handleTo(request);

			final URL resource = getClass().getClassLoader().getResource("static" + file);
			final String expected = "HTTP/1.1 200 OK \r\n" +
				"Content-Type: text/html;charset=utf-8 \r\n" +
				"Content-Length: 3796 \r\n" +
				"\r\n" +
				new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

			assertThat(actual.buildResponse())
				.isEqualTo(expected);
		}
	}
}
