package com.techcourse.web.util;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.model.User;

class LoginCheckerTest {

	@DisplayName("세션에 로그인 정보가 있으면 로그인 되었다고 판단한다.")
	@Test
	void isLoggedIn() {
		String sessionId = JsessionIdGenerator.generate();
		User user = new User("account", "password", "email@email.com");
		SessionManager.createSession(sessionId, user);

		List<String> headers = List.of("Cookie: JSESSIONID=" + sessionId);
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", headers, null);

		assertThat(LoginChecker.isLoggedIn(request)).isTrue();
	}

	@DisplayName("쿠키가 존재하지 않으면 로그인 되지 않았다고 판단한다.")
	@Test
	void isLoggedIn_WithNoCookie() {
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", List.of(), null);

		assertThat(LoginChecker.isLoggedIn(request)).isFalse();
	}

	@DisplayName("쿠키가 존재해도 JESSIONID가 없으면 로그인 되지 않았다고 판단한다.")
	@Test
	void isLoggedIn_WithNoJsessionId() {
		List<String> headers = List.of("Cookie: taste=strawberry");
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", headers, null);

		assertThat(LoginChecker.isLoggedIn(request)).isFalse();
	}

	@DisplayName("JESSIONID가 빈 문자열이면 로그인 되지 않았다고 판단한다.")
	@Test
	void isLoggedIn_WithEmptyJsessionId() {
		List<String> headers = List.of("Cookie: JSESSIONID=");
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", headers, null);

		assertThat(LoginChecker.isLoggedIn(request)).isFalse();
	}

	@DisplayName("JESSIONID에 해당하는 세션이 없으면 로그인 되지 않았다고 판단한다.")
	@Test
	void isLoggedIn_WithNoSession() {
		String sessionId = JsessionIdGenerator.generate();
		List<String> headers = List.of("Cookie: JSESSIONID=" + sessionId);
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", headers, null);

		assertThat(LoginChecker.isLoggedIn(request)).isFalse();
	}

	@DisplayName("세션에 로그인 정보가 없으면 로그인 되지 않았다고 판단한다.")
	@Test
	void isLoggedIn_WithNoUser() {
		String sessionId = JsessionIdGenerator.generate();
		SessionManager.createSession(sessionId, null);

		List<String> headers = List.of("Cookie: JSESSIONID=" + sessionId);
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", headers, null);

		assertThat(LoginChecker.isLoggedIn(request)).isFalse();
	}
}
