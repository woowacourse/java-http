package org.apache;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import support.HttpRequestFixture;

class StaticResourceControllerTest {

	StaticResourceController handler = new StaticResourceController();

	@Test
	void canHandle_true() throws IOException {
		// given
		final HttpRequest request = HttpRequestFixture.createGetMethod("/css/styles.css");

		// when
		boolean actual = handler.canHandle(request);

		// then
		assertThat(actual).isTrue();
	}

	@Test
	void canHandle_false() throws IOException {
		// given
		final HttpRequest request = HttpRequestFixture.createGetMethod("/css/styles.cs");

		// when
		boolean actual = handler.canHandle(request);

		// then
		assertThat(actual).isFalse();
	}

	@Test
	void handle() throws Exception {
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
		handler.doGet(request, response);

		// then
		var expected = String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Type: text/css;charset=utf-8 ",
			"Content-Length: 211991 ");

		assertThat(response.toString().startsWith(expected)).isTrue();
	}
}
