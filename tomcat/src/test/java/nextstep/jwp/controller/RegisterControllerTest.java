package nextstep.jwp.controller;

import static java.nio.file.Files.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequest.HttpRequestBuilder;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.jwp.db.InMemoryUserRepository;

class RegisterControllerTest {

	private static final RegisterController HANDLER = new RegisterController();

	@Nested
	@DisplayName("endpoint가 register고,")
	class IsSupported {

		@Test
		@DisplayName("http method가 get이면 true를 반환한다.")
		void getMethod() {
			final String plainRequest = String.join("\r\n",
				"GET /register HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");

			final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

			final boolean supported = HANDLER.isSupported(request);

			assertThat(supported)
				.isTrue();
		}

		@Test
		@DisplayName("http method가 get이면 true를 반환한다.")
		void postMethod() {
			final String plainRequest = String.join("\r\n",
				"POST /register HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");

			final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

			final boolean supported = HANDLER.isSupported(request);

			assertThat(supported)
				.isTrue();
		}

		@Test
		@DisplayName("httpMethod가 유효해도, endPoint가 다르면, false를 반환한다.")
		void invalidEndPoint() {
			final String plainRequest = String.join("\r\n",
				"POST /register1 HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");

			final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

			final boolean supported = HANDLER.isSupported(request);

			assertThat(supported)
				.isFalse();
		}

		@Test
		@DisplayName("endPoint가 유효해도 httpMethod가 다르면, false를 반환한다.")
		void invalidHttpMethod() {
			final String plainRequest = String.join("\r\n",
				"PUT /register1 HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");

			final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

			final boolean supported = HANDLER.isSupported(request);

			assertThat(supported)
				.isFalse();
		}
	}

	@Nested
	@DisplayName("/register endpoint로 접근 시 처리")
	class HandleTo {

		@Test
		@DisplayName("GET 메서드로 요청 시 /register.html을 반환한다.")
		void getCase() throws IOException {
			final String plainRequest = String.join("\r\n",
				"GET /register HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				"");
			final String file = "/register.html";
			final HttpRequest request = HttpRequestBuilder.from(plainRequest).build();

			final HttpResponse actual = HANDLER.handleTo(request);

			final URL resource = getClass().getClassLoader().getResource("static" + file);
			final String expected = "HTTP/1.1 200 OK \r\n" +
				"Content-Type: text/html;charset=utf-8 \r\n" +
				"Content-Length: 4319 \r\n" +
				"\r\n" +
				new String(readAllBytes(new File(resource.getFile()).toPath()));

			assertThat(actual.buildResponse())
				.isEqualTo(expected);
		}

		@Test
		@DisplayName("Post메서드로 요청 시 회원가입에 성공하는 경우 302 상태코드와 Location index.html을 반환한다.")
		void loginSuccess() throws IOException {
			final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
			final String plainRequest = String.join("\r\n",
				"POST /register HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				""
			);
			final HttpRequest request = HttpRequestBuilder.from(plainRequest)
				.body(requestBody)
				.build();

			final HttpResponse actual = HANDLER.handleTo(request);

			final String expected = "HTTP/1.1 302 Found \r\n" +
				"Content-Type: text/html;charset=utf-8 \r\n" +
				"Content-Length: 0 \r\n" +
				"Location: http://localhost:8080/index.html \r\n" +
				"\r\n";

			assertAll(
				() -> assertThat(actual.buildResponse())
					.isEqualTo(expected),
				() -> assertThat(InMemoryUserRepository.findByAccount("gugu"))
					.isPresent()
			);
		}
	}
}
