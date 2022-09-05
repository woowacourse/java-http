package org.apache.coyote.controller;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.controller.ErrorController;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;

public class RequestMapping {

	private static final String EXTENSION_DELIMITER = ".";

	private static final Map<String, Controller> apiControllers;
	private static final Controller ERROR_CONTROLLER = new ErrorController();
	private static final Controller STATIC_CONTROLLER = new StaticResourceController();

	static {
		apiControllers = new HashMap<>();
		apiControllers.put("/", new HelloController());
		apiControllers.put("/login", new LoginController());
		apiControllers.put("/register", new RegisterController());
	}

	public static Controller getController(String url) {
		if (isStaticResourceRequest(url)) {
			return STATIC_CONTROLLER;
		}
		return apiControllers.getOrDefault(url, ERROR_CONTROLLER);
	}

	private static boolean isStaticResourceRequest(String url) {
		return url.contains(EXTENSION_DELIMITER);
	}
}
