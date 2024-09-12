package com.techcourse.web.controller;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.util.JsessionIdGenerator;

class LoginControllerTest {

	@DisplayName("/login 으로 시작하는 페이지를 처리할 수 있다.")
	@Test
	void isSupport() {
		HttpRequest request = new HttpRequest("GET /login?account=hi HTTP/1.1", null, null);
		Controller controller = LoginController.getInstance();

		boolean isSupport = controller.isSupport(request);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("이미 로그인 된 회원이 GET /login 요청을 보내면 /index.html로 리다이렉트한다.")
	@Test
	void alreadyLoggedIn() throws Exception {
		User user = InMemoryUserRepository.findByAccount("gugu").get();
		String sessionId = JsessionIdGenerator.generate();
		SessionManager.createSession(sessionId, user);

		List<String> headers = List.of("Host: example.com", "Accept: text/html", "Cookie: JSESSIONID=" + sessionId);
		HttpRequest request = new HttpRequest("GET /login HTTP/1.1", headers, null);
		HttpResponse response = new HttpResponse();

		Controller controller = LoginController.getInstance();
		controller.service(request, response);

		assertThat(controller.isSupport(request)).isTrue();

		assertThat(response)
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/index.html"));
	}

	@DisplayName("로그인에 성공하면 /index.html로 리다이렉트한다.")
	@Test
	void succeedLogin() throws Exception {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("POST /login HTTP/1.1", headers, "account=gugu&password=password");
		HttpResponse response = new HttpResponse();

		Controller controller = LoginController.getInstance();
		controller.service(request, response);

		assertThat(controller.isSupport(request)).isTrue();
		assertThat(response)
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/index.html"));
	}

	@DisplayName("로그인에 성공하면 새로운 JSSEIONID를 발급한다.")
	@Test
	void createCookie() throws Exception {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("POST /login HTTP/1.1", headers, "account=gugu&password=password");
		HttpResponse response = new HttpResponse();

		Controller controller = LoginController.getInstance();
		controller.service(request, response);

		assertThat(controller.isSupport(request)).isTrue();
		assertThat(response)
			.extracting("header")
			.extracting("headers")
			.extracting("Set-Cookie")
			.isNotNull();
	}

	@DisplayName("로그인에 실패하면 /401.html 로 리다이렉트한다.")
	@Test
	void handle_whenLoginFailed() throws Exception {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("POST /login HTTP/1.1", headers, "account=hi&password=hello");
		HttpResponse response = new HttpResponse();

		Controller controller = LoginController.getInstance();
		controller.service(request, response);

		assertThat(controller.isSupport(request)).isTrue();
		assertThat(response)
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/401.html"));
	}
}
