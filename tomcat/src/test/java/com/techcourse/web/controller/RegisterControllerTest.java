package com.techcourse.web.controller;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

	@DisplayName("GET /register 요청을 처리할 수 있다.")
	@Test
	void isSupport_WhenRequestMethodIsGet() {
		HttpRequest request = new HttpRequest("GET /register HTTP/1.1", null, null);
		Controller controller = RegisterController.getInstance();

		boolean isSupport = controller.isSupport(request);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("POST /register 요청을 처리할 수 있다.")
	@Test
	void isSupport_WhenRequestMethodIsPost() {
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", null, null);
		Controller controller = RegisterController.getInstance();

		boolean isSupport = controller.isSupport(request);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("회원가입 페이지를 반환한다.")
	@Test
	void doGet() throws Exception {
		HttpRequest request = new HttpRequest("GET /register HTTP/1.1", List.of(), null);
		HttpResponse response = new HttpResponse();

		Controller controller = RegisterController.getInstance();
		controller.service(request, response);

		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.OK);
	}

	@DisplayName("회원가입에 성공한다.")
	@Test
	void register() throws Exception {
		String body = "account=sangdol&password=123&email=hello@naver.com";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		HttpResponse response = new HttpResponse();

		Controller controller = RegisterController.getInstance();
		controller.service(request, response);

		assertThat(response)
			.extracting("header")
			.extracting("headers")
			.extracting("Location")
			.isEqualTo(List.of("/index.html"));
	}

	@DisplayName("이미 존재하는 계정이면 400 에러를 반환한다.")
	@Test
	void register_whenExistingAccount() throws Exception {
		String body = "account=gugu&password=123&email=hello@naver.coom";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		HttpResponse response = new HttpResponse();

		Controller controller = RegisterController.getInstance();
		controller.service(request, response);

		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.BAD_REQUEST);
	}

	@DisplayName("회원가입을 위해서는 모든 값이 필요하다.")
	@Test
	void register_whenNotExistValue() throws Exception {
		String body = "account=123&password=123";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		HttpResponse response = new HttpResponse();

		Controller controller = RegisterController.getInstance();
		controller.service(request, response);

		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.BAD_REQUEST);
	}

	@DisplayName("빈 값을 입력할 수 없다.")
	@Test
	void register_whenEmptyValue() throws Exception {
		String body = "account=123&password= &email=email@email.com";
		HttpRequest request = new HttpRequest("POST /register HTTP/1.1", List.of(), body);
		HttpResponse response = new HttpResponse();

		Controller controller = RegisterController.getInstance();
		controller.service(request, response);

		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.BAD_REQUEST);
	}
}
