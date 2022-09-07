package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.*;
import static support.RequestFixture.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class StaticResourceControllerTest {

	@DisplayName("/index.html 요청이 들어오면 index.html을 출력한다.")
	@Test
	void index() throws IOException {
		// when
		StubSocket socket = HTML_요청("index");

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
		StubSocket socket = HTML_요청("notFound");

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
}
