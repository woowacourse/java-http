package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMapperTest {

	@DisplayName("/ 요청에는 RootPageHandler를 반환한다.")
	@Test
	void findHandler_WhenRequestRootPage() {
		String requestPath = "/";

		Class<? extends Handler> expectedHandler = RootPageHandler.class;

		runTest(requestPath, expectedHandler);
	}

	@DisplayName("/login 요청에는 LoginHandler를 반환한다.")
	@Test
	void findHandler_WhenRequestLogin() {
		String requestPath = "/login";

		Class<? extends Handler> expectedHandler = LoginHandler.class;

		runTest(requestPath, expectedHandler);
	}

	@DisplayName("정적 리소스 요청에는 ResourceHandler를 반환한다.")
	@Test
	void findHandler_WhenRequestResource() {
		String requestPath = "/css/test.css";

		Class<? extends Handler> expectedHandler = ResourceHandler.class;

		runTest(requestPath, expectedHandler);
	}

	@DisplayName("찾는 핸들러가 없는 경우 예외을 던진다.")
	@Test
	void findHandler_WhenHandlerNotFound() {
		HttpRequest request = new HttpRequest("GET /not-found HTTP/1.1", List.of(), null);

		assertThatThrownBy(() -> HandlerMapper.findHandler(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("handler not found. path: /not-found, method: GET");
	}

	private void runTest(String requestPath, Class<? extends Handler> expectedHandler) {
		HttpRequest request = new HttpRequest("GET " + requestPath + " HTTP/1.1", List.of(), null);

		Handler handler = HandlerMapper.findHandler(request);

		assertThat(handler).isInstanceOf(expectedHandler);
	}
}
