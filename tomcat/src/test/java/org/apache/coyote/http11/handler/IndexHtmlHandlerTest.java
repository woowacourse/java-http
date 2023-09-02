package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexHtmlHandlerTest {

	@Test
	@DisplayName("index.html을 핸들링해줄 수 있다.")
	void handleTo() throws IOException {
		//given
		final String httpRequest = String.join("\r\n",
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final HttpRequest request = HttpRequest.from(httpRequest);
		final IndexHtmlHandler handler = new IndexHtmlHandler();
		//when
		final HttpResponse response = handler.handleTo(request);
		//then
		final URL resource = getClass().getClassLoader().getResource("static/index.html");
		final String expected = "HTTP/1.1 200 OK \r\n" +
			"Content-Type: text/html;charset=utf-8 \r\n" +
			"Content-Length: 5564 \r\n" +
			"\r\n" +
			new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

		assertThat(response.buildResponse())
			.isEqualTo(expected);
	}
}
