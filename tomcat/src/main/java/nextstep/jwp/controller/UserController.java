package nextstep.jwp.controller;

import org.apache.coyote.http11.mapping.HandlerMapping;

public class UserController {

	private static final HandlerMapping HANDLER_MAPPING = HandlerMapping.getInstance();

	public UserController() {
		map("/", "/helloWorld.html", "OK");
		map("/login", "/login.html", "OK");
	}

	public void map(final String url, final String resource, final String statusCode) {
		HANDLER_MAPPING.add(url, resource, statusCode);
	}
}
