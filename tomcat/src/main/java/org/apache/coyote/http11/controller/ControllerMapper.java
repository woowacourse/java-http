package org.apache.coyote.http11.controller;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public enum ControllerMapper {

	ROOT_CONTROLLER((path) -> path.equals("/"), new RootController()),
	LOGIN_CONTROLLER((path) -> path.startsWith("/login"), new LoginController()),
	RESOURCE_CONTROLLER((path) -> path.contains("."), new ResourceController());

	private final Predicate<String> regex;
	private final Handler handler;

	ControllerMapper(Predicate<String> regex, Handler handler) {
		this.regex = regex;
		this.handler = handler;
	}

	public static Handler findController(String path) {
		return Arrays.stream(values())
			.filter(it -> it.regex.test(path))
			.map(ControllerMapper::getHandler)
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("일치하는 Handler 를 찾을 수 없습니다."));
	}

	public Handler getHandler() {
		return handler;
	}
}
