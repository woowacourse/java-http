package com.techcourse.web.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;

public class HandlerMapper {

	private static final List<Handler> handlers = new ArrayList<>();

	static {
		handlers.add(RootPageHandler.getInstance());
		handlers.add(ResourceHandler.getInstance());
		handlers.add(LoginHandler.getInstance());
	}

	public static Handler findHandler(HttpRequest httpRequest) {
		HttpRequestLine requestLine = httpRequest.getRequestLine();
		return handlers.stream()
			.filter(handler -> handler.isSupport(requestLine))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("handler not found. " +
				"path: " + requestLine.getRequestPath() + ", method: " + requestLine.getMethod())
			);
	}
}
