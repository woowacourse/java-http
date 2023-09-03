package org.apache.coyote.http11.handler.exception;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InternalServerErrorHandlerTest {

	@Test
	@DisplayName("500 상태코드와 500.html 페이지를 반환한다.")
	void handleTo() throws IOException {
		final InternalServerErrorHandler handler = new InternalServerErrorHandler();

		final HttpResponse actual = handler.handleTo();

		final URL resource = getClass().getClassLoader().getResource("static/500.html");
		final String expected = "HTTP/1.1 500 Internal Server Error \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 2408 \r\n" +
			"\r\n" +
			new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

		assertThat(actual.buildResponse())
			.isEqualTo(expected);
	}
}
