package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.request.HttpRequestUrl;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RootPageHandlerTest {

	@DisplayName("/ 경로에 대한 요청을 처리할 수 있다.")
	@Test
	void isSupport() {
		HttpRequestUrl requestUrl = HttpRequestUrl.from("/");
		HttpRequestLine requestLine = new HttpRequestLine("GET", requestUrl, "HTTP/1.1");
		RootPageHandler rootPageHandler = RootPageHandler.getInstance();

		boolean isSupport = rootPageHandler.isSupport(requestLine);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("GET 이외의 요청은 처리하지 않는다.")
	@Test
	void isNotSupport() {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("POST / HTTP/1.1", headers, null);

		RootPageHandler rootPageHandler = RootPageHandler.getInstance();

		// then
		assertThat(rootPageHandler.isSupport(request.getRequestLine())).isFalse();
	}

	@DisplayName("/ 요청을 처리한다.")
	@Test
	void isSupportWithQuery() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("GET / HTTP/1.1", headers, null);

		RootPageHandler rootPageHandler = RootPageHandler.getInstance();

		// then
		HttpResponse response = rootPageHandler.handle(request);
		assertThat(rootPageHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.OK);
		assertThat(response)
			.extracting("body")
			.extracting("content")
			.isEqualTo("Hello world!".getBytes());
	}
}
