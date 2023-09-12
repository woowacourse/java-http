package nextstep.jwp.handler;

import static org.apache.coyote.response.HeaderType.*;
import static org.apache.coyote.response.StatusCode.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.Map;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpProtocolVersion;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.request.RequestUri;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StatusCode;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RegisterRequestHandlerTest {

	@Test
	void get요청시_회원가입페이지_return() {
		// given
		final var registerRequestHandler = new RegisterRequestHandler();
		final var requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/register"),
			HttpProtocolVersion.HTTP11);
		final var request = new Request(requestLine, RequestHeader.empty(), RequestBody.empty());
		final var response = new Response();

		// when
		registerRequestHandler.doGet(request, response);

		// then
		assertSoftly(softly -> {
			softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.OK);
			softly.assertThat(response.getHeader().getHeaders().get(CONTENT_TYPE))
				.contains(MimeType.HTML.getValue());
			softly.assertThat(response.getResponseBody()).contains("register");
		});
	}

	@Nested
	class post요청 {

		@Test
		void account와_password와_email이_없으면_400페이지로_redirect() {
			// given
			final var registerRequestHandler = new RegisterRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.POST, new RequestUri("/register"),
				HttpProtocolVersion.HTTP11);
			final var request = new Request(requestLine, RequestHeader.empty(), RequestBody.empty());
			final var response = new Response();

			// when
			registerRequestHandler.doPost(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.FOUND);
				softly.assertThat(response.getHeader().getHeaders().get(LOCATION))
					.isEqualTo(BAD_REQUEST.getResourcePath());
			});
		}

		@Test
		void 중복된_account면_400페이지로_redirect() {
			// given
			final var registerRequestHandler = new RegisterRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.POST, new RequestUri("/register"),
				HttpProtocolVersion.HTTP11);
			final var requestBody = new RequestBody(
				Map.of("account", "gugu", "password", "pw", "email", "ash@wooteco.com"));
			final var request = new Request(requestLine, RequestHeader.empty(), requestBody);
			final var response = new Response();

			// when
			registerRequestHandler.doPost(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.FOUND);
				softly.assertThat(response.getHeader().getHeaders().get(LOCATION))
					.isEqualTo(BAD_REQUEST.getResourcePath());
			});
		}

		@Test
		void 회원가입_성공() {
			// given
			final var registerRequestHandler = new RegisterRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.POST, new RequestUri("/register"),
				HttpProtocolVersion.HTTP11);
			final var requestBody = new RequestBody(
				Map.of("account", "ash2", "password", "pw", "email", "ash@wooteco.com"));
			final var request = new Request(requestLine, RequestHeader.empty(), requestBody);
			final var response = new Response();

			// when
			registerRequestHandler.doPost(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.FOUND);
				softly.assertThat(response.getHeader().getHeaders().get(LOCATION)).isEqualTo("/index.html");
			});
		}
	}
}
