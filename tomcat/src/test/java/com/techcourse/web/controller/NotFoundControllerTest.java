package com.techcourse.web.controller;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotFoundControllerTest {

	@DisplayName("NotFoundController는 어떤 요청도 직접 처리하지 않는다.")
	@Test
	void isSupport() {
		NotFoundController controller = NotFoundController.getInstance();

		boolean isSupport = controller.isSupport(null);

		assertThat(isSupport).isFalse();
	}

	@DisplayName("404 에러 페이지를 반환한다.")
	@Test
	void service() throws Exception {
		NotFoundController controller = NotFoundController.getInstance();
		HttpRequest request = new HttpRequest("GET / HTTP/1.1", null, null);
		HttpResponse response = new HttpResponse();

		controller.service(request, response);

		assertThat(response)
			.extracting("startLine")
			.extracting("statusCode")
			.isEqualTo(HttpStatusCode.NOT_FOUND);
	}
}
