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

class RegisterHandlerTest {

	@DisplayName("GET /register 요청을 처리한다.")
	@Test
	void isSupport() {
		HttpRequestUrl requestUrl = HttpRequestUrl.from("/register");
		HttpRequestLine requestLine = new HttpRequestLine("GET", requestUrl, "HTTP/1.1");
		RegisterHandler handler = RegisterHandler.getInstance();

		boolean isSupport = handler.isSupport(requestLine);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("회원가입에 성공한다.")
	@Test
	void register() throws IOException {
		String body = "account=sangdol&password=123&email=hello@naver.com";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		RegisterHandler handler = RegisterHandler.getInstance();

		assertThat(handler.isSupport(request.getRequestLine())).isTrue();
		assertThat(handler.handle(request))
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/index.html"));
	}

	@DisplayName("이미 존재하는 계정이면 400 에러를 반환한다.")
	@Test
	void register_whenExistingAccount() throws IOException {
		String body = "account=gugu&password=123&email=hello@naver.coom";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		RegisterHandler handler = RegisterHandler.getInstance();

		assertThat(handler.isSupport(request.getRequestLine())).isTrue();
		assertThat(handler.handle(request))
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.BAD_REQUEST);
	}

	@DisplayName("회원가입을 위해서는 모든 값이 필요하다.")
	@Test
	void register_whenNotExistValue() throws IOException {
		String body = "account=123&password=123";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		RegisterHandler handler = RegisterHandler.getInstance();

		assertThat(handler.isSupport(request.getRequestLine())).isTrue();
		assertThat(handler.handle(request))
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.BAD_REQUEST);
	}

	@DisplayName("빈 값을 입력할 수 없다.")
	@Test
	void register_whenEmptyValue() throws IOException {
		String body = "account=123&password= &email=email@email.com";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		RegisterHandler handler = RegisterHandler.getInstance();

		assertThat(handler.isSupport(request.getRequestLine())).isTrue();
		assertThat(handler.handle(request))
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.BAD_REQUEST);
	}
}
