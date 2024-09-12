package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.web.Resource;
import com.techcourse.web.controller.Controller;
import com.techcourse.web.controller.LoginController;
import com.techcourse.web.controller.NotFoundController;
import com.techcourse.web.controller.ResourceController;
import com.techcourse.web.controller.RootPageController;

class RequestMappingTest {

	@DisplayName("/ 요청에는 RootPageController를 반환한다.")
	@Test
	void findController_WhenRequestRootPage() {
		String requestPath = "/";

		Class<RootPageController> expectedType = RootPageController.class;

		runTest(requestPath, expectedType);
	}

	@DisplayName("/login 요청에는 LoginController를 반환한다.")
	@Test
	void findController_WhenRequestLogin() {
		String requestPath = "/login";

		Class<LoginController> expectedType = LoginController.class;

		runTest(requestPath, expectedType);
	}

	@DisplayName("정적 리소스 요청에는 ResourceController를 반환한다.")
	@Test
	void findController_WhenRequestResource() {
		String requestPath = "/css/test.css";

		Class<ResourceController> expectedType = ResourceController.class;

		runTest(requestPath, expectedType);
	}

	@DisplayName("찾는 핸들러가 없는 경우 404 핸들러를 반환한다.")
	@Test
	void findController_WhenControllerNotFound() {
		String requestPath = "/notfound";

		Class<NotFoundController> expectedType = NotFoundController.class;

		runTest(requestPath, expectedType);
	}

	private void runTest(String requestPath, Class<? extends Controller> expectedType) {
		HttpRequest request = new HttpRequest("GET " + requestPath + " HTTP/1.1", List.of(), null);

		Controller handler = RequestMapping.getInstance().getController(request);

		assertThat(handler).isExactlyInstanceOf(expectedType);
	}
}
