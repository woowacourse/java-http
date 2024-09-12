package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.request.HttpRequestUrl;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceHandlerTest {


	@DisplayName("정적 리소스 요청을 처리할 수 있다.")
	@Test
	void isSupport() {
		HttpRequestUrl requestUrl = HttpRequestUrl.from("/css/test.css");
		HttpRequestLine requestLine = new HttpRequestLine("GET", requestUrl, "HTTP/1.1");
		ResourceHandler handler = ResourceHandler.getInstance();

		boolean isSupport = handler.isSupport(requestLine);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("GET 이외의 요청은 처리하지 않는다.")
	@Test
	void isNotSupport() {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("POST /js/test.js HTTP/1.1", headers, null);

		ResourceHandler handler = ResourceHandler.getInstance();

		// then
		assertThat(handler.isSupport(request.getRequestLine())).isFalse();
	}

	@DisplayName("정적 리소스에 대한 요청을 처리한다.")
	@Test
	void isSupportWithQuery() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", headers, null);

		ResourceHandler handler = ResourceHandler.getInstance();

		// then
		HttpResponse response = handler.handle(request);
		assertThat(handler.isSupport(request.getRequestLine())).isTrue();
		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.OK);
	}
}
