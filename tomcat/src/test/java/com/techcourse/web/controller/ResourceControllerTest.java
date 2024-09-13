package com.techcourse.web.controller;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ResourceControllerTest {

	@DisplayName("정적 리소스 요청을 처리할 수 있다.")
	@Test
	void isSupport() {
		HttpRequest request = new HttpRequest("GET /css/test.css HTTP/1.1", List.of(), null);
		Controller controller = ResourceController.getInstance();

		boolean isSupport = controller.isSupport(request);

		assertThat(isSupport).isTrue();
	}

	@DisplayName("GET 이외의 요청은 처리하지 않는다.")
	@Test
	void isNotSupport() {
		HttpRequest request = new HttpRequest("POST /css/test.css HTTP/1.1", List.of(), null);
		Controller controller = ResourceController.getInstance();

		boolean isSupport = controller.isSupport(request);

		assertThat(isSupport).isFalse();
	}

	@DisplayName("정적 리소스에 대한 요청을 처리한다.")
	@Test
	void service() throws Exception {
		List<String> headers = List.of("Host: example.com", "Accept: text/html");
		HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1", headers, null);
		HttpResponse response = new HttpResponse();

		Controller controller = ResourceController.getInstance();
		controller.service(request, response);

		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.OK);
	}
}
