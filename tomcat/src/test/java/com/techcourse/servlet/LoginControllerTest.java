package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.servlet.LoginController;

import support.HttpRequestFixture;

class LoginControllerTest {

	@DisplayName("세션이 존재하지 않을 땐 login.html 파일을 반환한다.")
	@Test
	void handle_GET_returnLoginHtml() throws Exception {
		// given
		SessionManager sessionManager = SessionManager.getInstance();
		LoginController handler = new LoginController(sessionManager);
		HttpRequest request = HttpRequestFixture.createGetMethod("/login");
		HttpResponse response = HttpResponse.empty();

		// when
		handler.doGet(request, response);

		// then
		URL resource = Http11Processor.class.getClassLoader().getResource("static/login.html");
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		var responseBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		assertThat(response.getResponseBody()).isEqualTo(responseBody);
		assertThat(response.getHeaders())
			.anyMatch(header -> header.startsWith("Location:"));
	}

	@DisplayName("쿠키에 유효한 세션이 존재할 경우 index.html 파일을 반환한다.")
	@Test
	void handle_GET_returnRedirectUri() throws Exception {
		// given
		SessionManager sessionManager = SessionManager.getInstance();
		UUID uuid = UUID.randomUUID();
		sessionManager.add(new Session(uuid.toString()));
		LoginController controller = new LoginController(sessionManager);
		HttpRequest request = HttpRequestFixture.createGetMethodWithSessionId("/login", uuid.toString());
		HttpResponse response = HttpResponse.empty();
		// when
		controller.doGet(request, response);

		// then
		assertThat(response.getHeaders()).contains("Location: /index.html");
	}

	@DisplayName("로그인 POST 요청시 세션을 생성하여 Cookie header 에 전달한다.")
	@Test
	void handle_POST_returnSessionId() throws Exception {
		// given
		LoginController controller = new LoginController(SessionManager.getInstance());
		HttpRequest request = HttpRequestFixture.createLoginPostMethod();
		HttpResponse response = HttpResponse.empty();

		// when
		controller.doPost(request, response);

		// then
		assertThat(response.getHeaders())
			.anyMatch(header -> header.startsWith("Set-Cookie: "));
	}

	@DisplayName("로그인 POST 요청시 세션을 생성하여 Cookie header 에 전달한다.")
	@Test
	void handle_POST_withInvalidPassword() throws Exception {
		// given
		LoginController controller = new LoginController(SessionManager.getInstance());
		HttpRequest request = HttpRequestFixture.createLoginPostMethodWithInvalidPassword();
		HttpResponse response = HttpResponse.empty();

		// when
		controller.doPost(request, response);

		// then
		assertThat(response.getHeaders()).contains("Location: /401.html");
	}
}
