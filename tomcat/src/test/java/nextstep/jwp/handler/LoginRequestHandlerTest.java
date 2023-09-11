package nextstep.jwp.handler;

import static org.apache.coyote.response.HeaderType.*;
import static org.apache.coyote.response.StatusCode.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Cookie;
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

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class LoginRequestHandlerTest {

	@Nested
	class get요청 {

		@Test
		void 세션이_있으면_index로_redirect() {
			// given
			final var sessionId = "abc";
			final var session = new Session(sessionId);
			session.setAttribute("account", "ash");
			SessionManager.add(session);

			final var loginRequestHandler = new LoginRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/login"),
				HttpProtocolVersion.HTTP11);
			final var header = new RequestHeader(Collections.emptyMap(), List.of(Cookie.session(sessionId)));
			final var request = new Request(requestLine, header, null);
			final var response = new Response();

			// when
			loginRequestHandler.doGet(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.FOUND);
				softly.assertThat(response.getHeader().getHeaders().get(LOCATION)).isEqualTo("/index.html");
			});
		}

		@Test
		void 세션이_없으면_로그인페이지_반환() {
			// given
			final var loginRequestHandler = new LoginRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/login"),
				HttpProtocolVersion.HTTP11);
			final var request = new Request(requestLine, RequestHeader.empty(), RequestBody.empty());
			final var response = new Response();

			// when
			loginRequestHandler.doGet(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.OK);
				softly.assertThat(response.getHeader().getHeaders().get(CONTENT_TYPE))
					.contains(MimeType.HTML.getValue());
				softly.assertThat(response.getResponseBody()).contains("login");
			});
		}

		@Test
		void 세션이_유효하지_않으면_로그인페이지_반환() {
			// given
			final var sessionId = "abc";
			final var session = new Session(sessionId);
			session.setAttribute("account", "ash");

			final var loginRequestHandler = new LoginRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/login"),
				HttpProtocolVersion.HTTP11);
			final var header = new RequestHeader(Collections.emptyMap(), List.of(Cookie.session(sessionId)));
			final var request = new Request(requestLine, header, null);
			final var response = new Response();

			// when
			loginRequestHandler.doGet(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.OK);
				softly.assertThat(response.getHeader().getHeaders().get(CONTENT_TYPE))
					.contains(MimeType.HTML.getValue());
				softly.assertThat(response.getResponseBody()).contains("login");
			});
		}
	}

	@Nested
	class post요청 {

		@Test
		void account와_password가_없으면_400페이지로_redirect() {
			// given
			final var loginRequestHandler = new LoginRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.POST, new RequestUri("/login"),
				HttpProtocolVersion.HTTP11);
			final var request = new Request(requestLine, RequestHeader.empty(), RequestBody.empty());
			final var response = new Response();

			// when
			loginRequestHandler.doPost(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.FOUND);
				softly.assertThat(response.getHeader().getHeaders().get(LOCATION))
					.isEqualTo(BAD_REQUEST.getResourcePath());
			});
		}

		@Test
		void db에_없는_유저이면_401페이지로_redirect() {
			// given
			final var loginRequestHandler = new LoginRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.POST, new RequestUri("/login"),
				HttpProtocolVersion.HTTP11);
			final var requestBody = new RequestBody(Map.of("account", "ash", "password", "pw"));
			final var request = new Request(requestLine, RequestHeader.empty(), requestBody);
			final var response = new Response();

			// when
			loginRequestHandler.doPost(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.FOUND);
				softly.assertThat(response.getHeader().getHeaders().get(LOCATION))
					.isEqualTo(UNAUTHORIZED.getResourcePath());
			});
		}

		@Test
		void 로그인_성공() {
			// given
			final var account = "ash";
			final var password = "pw";
			final var user = new User(account, password, "ash@wooteco.com");
			InMemoryUserRepository.save(user);

			final var loginRequestHandler = new LoginRequestHandler();
			final var requestLine = new RequestLine(HttpMethod.POST, new RequestUri("/login"),
				HttpProtocolVersion.HTTP11);
			final var requestBody = new RequestBody(Map.of("account", account, "password", password));
			final var request = new Request(requestLine, RequestHeader.empty(), requestBody);
			final var response = new Response();

			// when
			loginRequestHandler.doPost(request, response);

			// then
			assertSoftly(softly -> {
				softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.FOUND);
				softly.assertThat(response.getHeader().getHeaders().get(LOCATION)).isEqualTo("/index.html");
			});
		}
	}
}
