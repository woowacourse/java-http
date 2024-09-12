package com.techcourse.web.controller;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

	@DisplayName("GET / POST 요청이 아니면 BAD_REQUEST 상태코드를 반환한다.")
	@Test
	void service() throws Exception {
		Controller controller = new AbstractController() {
			@Override
			public boolean isSupport(HttpRequest request) {
				return false;
			}
		};

		HttpRequest request = new HttpRequest("DELETE / HTTP/1.1", null, null);
		HttpResponse response = new HttpResponse();

		controller.service(request, response);

		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.BAD_REQUEST);
	}
}
