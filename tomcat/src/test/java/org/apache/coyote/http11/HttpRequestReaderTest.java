package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestReaderTest {

	@DisplayName("Http 요청 메시지를 읽는다.")
	@Test
	void read() throws IOException {
		String httpRequest = String.join("\r\n",
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");

		InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
		HttpRequest request = HttpRequestReader.read(inputStream);

		assertThat(request).satisfies(r -> {
			assertThat(r.getMethod()).isEqualTo("GET");
			assertThat(r.getRequestPath()).isEqualTo("/index.html");
			assertThat(r.getProtocol()).isEqualTo("HTTP/1.1");
			assertThat(r.getHeaders()).satisfies(headers -> {
				assertThat(headers).containsEntry("Host", "localhost:8080");
				assertThat(headers).containsEntry("Connection", "keep-alive");
			});
		});
	}
}