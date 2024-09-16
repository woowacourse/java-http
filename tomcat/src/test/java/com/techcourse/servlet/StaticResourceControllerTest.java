package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.apache.catalina.servlets.StaticResourceController;
import support.HttpRequestFixture;

class StaticResourceControllerTest {

	StaticResourceController controller = new StaticResourceController();

	@DisplayName("지원하는 정적 파일 형식일 경우 true 반환.")
	@Test
	void canHandle_true() throws IOException {
		// given
		final HttpRequest request = HttpRequestFixture.createGetMethod("/css/styles.css");

		// when
		boolean actual = controller.canHandle(request);

		// then
		assertThat(actual).isTrue();
	}

	@DisplayName("지원하지 않는 정적 파일 형식일 경우 false 반환.")
	@Test
	void canHandle_false() throws IOException {
		// given
		final HttpRequest request = HttpRequestFixture.createGetMethod("/css/styles.cs");

		// when
		boolean actual = controller.canHandle(request);

		// then
		assertThat(actual).isFalse();
	}

	@DisplayName("정적 파일 응답을 반환한다.")
	@Test
	void service() throws Exception {
		// given
		String requestString = String.join("\r\n",
			"GET /css/styles.css HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		final HttpRequest request = HttpRequest.from(new ByteArrayInputStream(requestString.getBytes()));
		final HttpResponse response = HttpResponse.empty();

		// when
		controller.service(request, response);

		// then
		var expected = String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Type: text/css ",
			"Content-Length: 211991 ");

		assertThat(response.toString().startsWith(expected)).isTrue();
	}
}
