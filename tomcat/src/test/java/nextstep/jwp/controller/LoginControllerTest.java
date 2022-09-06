package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.DatabaseIsolation;
import support.StubSocket;

class LoginControllerTest extends DatabaseIsolation {

	@DisplayName("/login으로 GET 요청을 보내면 login.html을 반환한다.")
	@Test
	void login_html() throws IOException {
		// given
		final String httpRequest = String.join("\r\n",
			"GET /login HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

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
		// given
		final String registerRequest = String.join("\r\n",
			"POST /register HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Type: application/x-www-form-urlencoded",
			"Content-Length: 30",
			"Accept: */*",
			"",
			"account=gugu&password=password&email=ldk980130@gmail.com");

		final var registerSocket = new StubSocket(registerRequest);
		new Http11Processor(registerSocket).process(registerSocket);

		final String httpRequest = String.join("\r\n",
			"POST /login HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Type: application/x-www-form-urlencoded",
			"Content-Length: 30",
			"",
			"account=gugu&password=password");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

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
		final String registerRequest = String.join("\r\n",
			"POST /register HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Type: application/x-www-form-urlencoded",
			"Content-Length: 30",
			"Accept: */*",
			"",
			"account=gugu&password=password&email=ldk980130@gmail.com");

		final var registerSocket = new StubSocket(registerRequest);
		new Http11Processor(registerSocket).process(registerSocket);

		final String loginRequest = String.join("\r\n",
			"POST /login HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Type: application/x-www-form-urlencoded",
			"Content-Length: 30",
			"",
			"account=gugu&password=password");

		final var loginSocket = new StubSocket(loginRequest);
		new Http11Processor(loginSocket).process(loginSocket);

		String jSessionId = loginSocket.output()
			.split("\r\n")[2]
			.split("JSESSIONID=")[1]
			.trim();

		final String httpRequest = String.join("\r\n",
			"GET /login HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Cookie: JSESSIONID=" + jSessionId,
			"",
			"");
		final var socket = new StubSocket(httpRequest);
		Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

		// then
		var expected = "HTTP/1.1 302 Found \r\n" +
			"Location: /index.html" + " \r\n";

		assertThat(socket.output()).isEqualTo(expected);
	}

	@DisplayName("로그인이 실패하면 401.html을 반환한다.")
	@Test
	void login_fail() throws IOException {
		// given
		final String httpRequest = String.join("\r\n",
			"POST /login HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Type: application/x-www-form-urlencoded",
			"Content-Length: 30",
			"",
			"account=gugugu&password=password");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

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
