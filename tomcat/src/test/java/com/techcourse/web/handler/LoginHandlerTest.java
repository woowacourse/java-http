package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.request.HttpRequestUrl;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.http.session.SessionManager;
import com.techcourse.web.util.JsessionIdGenerator;

class LoginHandlerTest {

	@DisplayName("/login 으로 시작하는 페이지를 처리한다.")
	@Test
	void isSupport() {
		HttpRequestUrl requestUrl = HttpRequestUrl.from("/login?account=hi");
		HttpRequestLine requestLine = new HttpRequestLine("GET", requestUrl, "HTTP/1.1");
		LoginHandler loginHandler = LoginHandler.getInstance();

		boolean isSupport = loginHandler.isSupport(requestLine);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("이미 로그인 된 회원이 GET /login 요청을 보내면 /index.html로 리다이렉트한다.")
	@Test
	void alreadyLoggedIn() throws IOException {
		User user = InMemoryUserRepository.findByAccount("gugu").get();
		String sessionId = JsessionIdGenerator.generate();
		SessionManager.createSession(sessionId, user);

		List<String> headers = List.of("Host: example.com", "Accept: text/html", "Cookie: JSESSIONID=" + sessionId);
		HttpRequest request = new HttpRequest("GET /login HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request))
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/index.html"));
	}

	@DisplayName("로그인에 성공하면 /index.html로 리다이렉트한다.")
	@Test
	void succeedLogin() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("GET /login?account=gugu&password=password HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request))
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/index.html"));
	}

	@DisplayName("로그인에 성공하고, 쿠키가 존재하지 않으면 쿠키를 생성한다.")
	@Test
	void createCookie() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("GET /login?account=gugu&password=password HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request))
			.extracting("header")
			.extracting("headers")
			.extracting("Set-Cookie")
			.isNotNull();
	}

	@DisplayName("로그인에 성공해도 쿠키가 존재하면 쿠키를 생성하지 않는다.")
	@Test
	void notCreateCookie() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html", "Cookie: JSESSIONID=1234");
		HttpRequest request = new HttpRequest("GET /login?account=gugu&password=password HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request)).extracting("header")
			.extracting("headers")
			.doesNotHave(new Condition<>(h -> ((Map<String, String>)h).containsKey("Set-Cookie"), "Set-Cookie"));
	}

	@DisplayName("로그인에 실패하면 /401.html 로 리다이렉트한다.")
	@Test
	void handle_whenLoginFailed() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("GET /login?account=hi&password=hellofail HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request))
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/401.html"));
	}

	@DisplayName("POST 요청은 처리하지 않는다.")
	@Test
	void isNotSupport() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("POST /login?account=hi&password=hello HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request))
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.NOT_FOUND);
	}
}
