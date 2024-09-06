package com.techcourse.web.handler;

import java.util.HashMap;
import java.util.Map;

public class RequestHandlerMapper {

	private static final RequestHandlerMapper instance = new RequestHandlerMapper();
	private static final Map<String, RequestHandler> handlerMap = new HashMap<>();

	static {
		handlerMap.put("/", PageHandler.getInstance());
		handlerMap.put("^.*\\.(html|css|js|ico)$", PageHandler.getInstance());
		handlerMap.put("^(\\/login)(?!\\.).*", LoginHandler.getInstance());
	}

	private RequestHandlerMapper() {
	}

	public static RequestHandlerMapper getInstance() {
		return instance;
	}

	public RequestHandler findHandler(String requestPath) {
		return handlerMap.entrySet().stream()
			.filter(entry -> requestPath.matches(entry.getKey()))
			.map(Map.Entry::getValue)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("handler not found: " + requestPath));
	}
}
