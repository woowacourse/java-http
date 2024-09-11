package org.apache;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.HttpRequestFixture;

class LoginRequestHandlerTest {

	@DisplayName("세션이 존재하지 않을 땐 login.html 파일을 반환한다.")
	@Test
	void handle_GET_returnLoginHtml() throws IOException {
		// given
		SessionManager sessionManager = SessionManager.getInstance();
		LoginRequestHandler handler = new LoginRequestHandler(sessionManager);
		HttpRequest request = HttpRequestFixture.createGetMethod("/login");

		// when
		HttpResponse response = handler.handle(request);

		// then
		URL resource = Http11Processor.class.getClassLoader().getResource("static/login.html");
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		var responseBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		assertThat(response.getResponseBody()).isEqualTo(responseBody);
	}

	@DisplayName("쿠키에 유효한 세션이 존재할 경우 index.html 파일을 반환한다.")
	@Test
	void handle_GET_returnRedirectUri() throws IOException {
		// given
		SessionManager sessionManager = SessionManager.getInstance();
		UUID uuid = UUID.randomUUID();
		sessionManager.add(new Session(uuid.toString()));
		LoginRequestHandler handler = new LoginRequestHandler(sessionManager);
		HttpRequest request = HttpRequestFixture.createGetMethodWithSessionId("/login", uuid.toString());

		// when
		HttpResponse response = handler.handle(request);

		// then
		assertThat(response.getHeaders()).contains("Location: /index.html");
	}

	@DisplayName("로그인 POST 요청시 세션을 생성하여 Cookie header 에 전달한다.")
	@Test
	void handle_POST_returnSessionId() throws IOException {
		// given
		LoginRequestHandler handler = new LoginRequestHandler(SessionManager.getInstance());
		HttpRequest request = HttpRequestFixture.createLoginPostMethod();

		// when
		HttpResponse response = handler.handle(request);

		// then
		assertThat(response.getHeaders())
			.anyMatch(header -> header.startsWith("Set-Cookie: "));
	}

	@DisplayName("로그인 POST 요청시 세션을 생성하여 Cookie header 에 전달한다.")
	@Test
	void handle_POST_withInvalidPassword() throws IOException {
		// given
		LoginRequestHandler handler = new LoginRequestHandler(SessionManager.getInstance());
		HttpRequest request = HttpRequestFixture.createLoginPostMethodWithInvalidPassword();

		// when
		HttpResponse response = handler.handle(request);

		// then
		assertThat(response.getHeaders()).contains("Location: /401.html");
	}
}
