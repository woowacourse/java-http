package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

	@DisplayName("/ 요청이 들어오면 Hello world!를 출력한다.")
	@Test
	void process() {
		// given
		final var socket = new StubSocket();
		final var processor = new Http11Processor(socket);

		// when
		processor.process(socket);

		// then
		var expected = String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Length: 12 ",
			"Content-Type: text/html;charset=utf-8 ",
			"",
			"Hello world!");

		assertThat(socket.output()).isEqualTo(expected);
	}

	@DisplayName("/index.html 요청이 들어오면 index.html을 출력한다.")
	@Test
	void index() throws IOException {
		// given
		final String httpRequest = String.join("\r\n",
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

		// then
		final URL resource = getClass().getClassLoader().getResource("static/index.html");
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		var expected = "HTTP/1.1 200 OK \r\n" +
			"Content-Length: " + responseBody.getBytes().length + " \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"\r\n" +
			responseBody;

		assertThat(socket.output()).isEqualTo(expected);
	}

	@DisplayName("없는 페이지 요청이 들어오면 404.html을 출력한다.")
	@Test
	void notFound() throws IOException {
		// given
		final String httpRequest = String.join("\r\n",
			"GET /notFound.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

		// then
		final URL resource = getClass().getClassLoader().getResource("static/404.html");
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		var expected = "HTTP/1.1 404 Not Found \r\n" +
			"Content-Length: " + responseBody.getBytes().length + " \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"\r\n" +
			responseBody;

		assertThat(socket.output()).isEqualTo(expected);
	}

	@DisplayName("로그인이 성공하면 /index.html로 리다이렉트 한다.")
	@Test
	void login() throws IOException {
		// given
		final String httpRequest = String.join("\r\n",
			"GET /login?account=gugu&password=password HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

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
			"GET /login?account=gugugu&password=password HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		final var socket = new StubSocket(httpRequest);
		final Http11Processor processor = new Http11Processor(socket);

		// when
		processor.process(socket);

		// then
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
