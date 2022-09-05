package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class RegisterControllerTest {

	@DisplayName("/register로 요청이 들어오면 register.html을 반환한다.")
	@Test
	void register_html() throws IOException {
		// given
		final String httpRequest = String.join("\r\n",
			"GET /register HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

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
}
