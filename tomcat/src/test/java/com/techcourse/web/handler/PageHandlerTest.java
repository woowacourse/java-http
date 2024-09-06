package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.web.HttpResponse;

class PageHandlerTest {

	@DisplayName("/ 경로 요청에 대한 응답을 생성한다.")
	@Test
	void getResource_WhenRequestPathIsRoot() {
		PageHandler handler = PageHandler.getInstance();

		HttpRequest httpRequest = new HttpRequest(Arrays.asList(
			"GET / HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Accept: text/html "
		));

		HttpResponse response = handler.handle(httpRequest);

		assertThat(response).satisfies(r -> {
			assertThat(r.getProtocol()).isEqualTo("HTTP/1.1");
			assertThat(r.getStatusCode().getCode()).isEqualTo(200);
			assertThat(r.getHeaders()).containsEntry("Content-Type", "text/html;charset=utf-8");
			assertThat(r.getHeaders()).containsEntry("Content-Length", "12");
			assertThat(r.getBody().getContent()).isEqualTo("Hello world!");
		});
	}

	@DisplayName("/index.html 경로 요청에 대한 응답을 생성한다.")
	@Test
	void getResource_WhenRequestPathIsIndexHtml() throws IOException {
		PageHandler handler = PageHandler.getInstance();

		HttpRequest httpRequest = new HttpRequest(Arrays.asList(
			"GET /index.html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Accept: text/html "
		));

		HttpResponse response = handler.handle(httpRequest);
		Path path = Path.of("src/main/resources/static/index.html");
		String content = Files.readString(path);

		assertThat(response).satisfies(r -> {
			assertThat(r.getProtocol()).isEqualTo("HTTP/1.1");
			assertThat(r.getStatusCode().getCode()).isEqualTo(200);
			assertThat(r.getHeaders()).containsEntry("Content-Type", "text/html;charset=utf-8");
			assertThat(r.getHeaders()).containsEntry("Content-Length", String.valueOf(content.getBytes().length));
			assertThat(r.getBody().getContent()).isEqualTo(content);
		});
	}
}
