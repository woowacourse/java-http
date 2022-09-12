package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class HelloControllerTest {

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

}
