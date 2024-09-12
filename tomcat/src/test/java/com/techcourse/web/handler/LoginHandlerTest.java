package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.request.HttpRequestUrl;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

	@DisplayName("/login 으로 시작하는 페이지를 처리한다.")
	@Test
	void isSupport() {
		HttpRequestUrl requestUrl = HttpRequestUrl.from("/login?account=hi");
		HttpRequestLine requestLine = new HttpRequestLine("GET", requestUrl, "HTTP/1.1");
		LoginHandler loginHandler = LoginHandler.getInstance();

		boolean isSupport = loginHandler.isSupport(requestLine);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("/login?account={account}&password={password} 요청을 처리한다.")
	@Test
	void isSupportWithQuery() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("GET /login?account=hi&password=hello HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		// then
		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request))
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.OK);
	}

	@DisplayName("POST 요청은 처리하지 않는다.")
	@Test
	void isNotSupport() throws IOException {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("POST /login?account=hi&password=hello HTTP/1.1", headers, null);

		LoginHandler loginHandler = LoginHandler.getInstance();

		// then
		assertThat(loginHandler.isSupport(request.getRequestLine())).isTrue();
		assertThat(loginHandler.handle(request))
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.NOT_FOUND);
	}
}
