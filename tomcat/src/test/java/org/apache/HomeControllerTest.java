package org.apache;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.HttpRequestFixture;

class HomeControllerTest {

	Controller controller = new HomeController();

	@DisplayName("루트 경로로 요청시 Hello world! 반환")
	@Test
	void service() throws Exception {
		// given
		String uri = "/";
		HttpRequest request = HttpRequestFixture.createGetMethod(uri);
		HttpResponse response = HttpResponse.empty();

		// when
		controller.service(request, response);

		// then
		assertThat(response.getResponseBody()).isEqualTo("Hello world!");
	}
}
