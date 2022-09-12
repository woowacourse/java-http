package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;
import static support.RequestFixture.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.DatabaseIsolation;
import support.StubSocket;

class RegisterControllerTest extends DatabaseIsolation {

	@DisplayName("/register로 GET 요청이 들어오면 register.html을 반환한다.")
	@Test
	void register_html() throws IOException {
		// when
		StubSocket socket = 회원가입_패이지_요청();

		// then
		final URL resource = getClass().getClassLoader().getResource("static/register.html");
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		var expected = "HTTP/1.1 200 OK \r\n" +
			"Content-Length: " + responseBody.getBytes().length + " \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"\r\n" +
			responseBody;

		assertThat(socket.output()).isEqualTo(expected);
	}

	@DisplayName("/register로 POST 요청이 들어오면 회원 가입을 하고 index.html로 리다이렉트 한다.")
	@Test
	void register() {
		//when
		StubSocket socket = 회원가입_요청("does", "does!", "does@mail.com");

		// then
		var expected = "HTTP/1.1 302 Found \r\n" +
			"Location: /index.html" + " \r\n";

		assertThat(socket.output()).isEqualTo(expected);
	}
}
