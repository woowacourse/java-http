package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

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
			"Content-Type: text/html;charset=utf-8 ",
			"Content-Length: 12 ",
			"",
			"Hello world!");

		assertThat(socket.output()).isEqualTo(expected);
	}

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
        /*
        윈도우 이슈로 content-length 변경 윈도우에서는 줄바꿈이 \r\n이라 2바이트고, 맥에서는 \n이라 1바이트로 인해 발생한 문제로 추측s
         */
		var expected = "HTTP/1.1 200 OK \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 5564 \r\n" +
			"\r\n" +
			new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

		assertThat(socket.output()).isEqualTo(expected);
	}
}
