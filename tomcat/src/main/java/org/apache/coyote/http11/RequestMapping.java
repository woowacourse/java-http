package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;

import com.techcourse.web.controller.Controller;
import com.techcourse.web.controller.LoginController;
import com.techcourse.web.controller.NotFoundController;
import com.techcourse.web.controller.RegisterController;
import com.techcourse.web.controller.ResourceController;
import com.techcourse.web.controller.RootPageController;

public class RequestMapping {

	private static final RequestMapping instance = new RequestMapping();
	private static final List<Controller> controllers = new ArrayList<>();

	static {
		controllers.add(RootPageController.getInstance());
		controllers.add(ResourceController.getInstance());
		controllers.add(LoginController.getInstance());
		controllers.add(RegisterController.getInstance());
	}

	private RequestMapping() {
	}

	public static RequestMapping getInstance() {
		return instance;
	}

	public Controller getController(HttpRequest request) {
		return controllers.stream()
			.filter(controller -> controller.isSupport(request))
			.findFirst()
			.orElse(NotFoundController.getInstance());
	}
}
