package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;
import static support.RequestFixture.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.DatabaseIsolation;
import support.StubSocket;

class LoginControllerTest extends DatabaseIsolation {

	@DisplayName("/login으로 GET 요청을 보내면 login.html을 반환한다.")
	@Test
	void login_html() throws IOException {
		// given
		StubSocket socket = 로그인_페이지_요청();

		// then
		final URL resource = getClass().getClassLoader().getResource("static/login.html");
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		var expected = "HTTP/1.1 200 OK \r\n" +
			"Content-Length: " + responseBody.getBytes().length + " \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"\r\n" +
			responseBody;

		assertThat(socket.output()).isEqualTo(expected);
	}

	@DisplayName("로그인이 성공하면 /index.html로 리다이렉트 한다.")
	@Test
	void login() {
		//given
		회원가입_요청("does", "does!", "does@mail.com");

		// when
		StubSocket socket = 로그인_요청("does", "does!");

		// then
		var expected = "HTTP/1.1 302 Found \r\n" +
			"Location: /index.html" + " \r\n" +
			"Set-Cookie: JSESSIONID=";

		assertThat(socket.output()).contains(expected);
	}

	@DisplayName("로그인을 한 후 로그인 페이지로 가면 index.html로 리다이렉트 된다.")
	@Test
	void login_get() {
		// given
		회원가입_요청("does", "does!", "does@mail.com");

		final var loginSocket = 로그인_요청("does", "does!");

		String jSessionId = loginSocket.output()
			.split("\r\n")[2]
			.split("JSESSIONID=")[1]
			.trim();


		// when
		StubSocket socket = 로그인_페이지_요청(jSessionId);

		// then
		var expected = "HTTP/1.1 302 Found \r\n" +
			"Location: /index.html" + " \r\n";

		assertThat(socket.output()).isEqualTo(expected);
	}

	@DisplayName("로그인이 실패하면 401.html을 반환한다.")
	@Test
	void login_fail() throws IOException {
		// when
		StubSocket socket = 로그인_요청("does", "does!");

		// then
		final URL resource = getClass().getClassLoader().getResource("static/401.html");
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		var expected = "HTTP/1.1 401 Unauthorized \r\n" +
			"Content-Length: " + responseBody.getBytes().length + " \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"\r\n" +
			responseBody;

		assertThat(socket.output()).isEqualTo(expected);
	}
}
