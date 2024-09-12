package com.techcourse.web.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseHeader;

public class HandlerMapper {

	private static final List<Handler> handlers = new ArrayList<>();

	static {
		handlers.add(RootPageHandler.getInstance());
		handlers.add(ResourceHandler.getInstance());
		handlers.add(LoginHandler.getInstance());
		handlers.add(RegisterHandler.getInstance());
	}

	public static Handler findHandler(HttpRequest httpRequest) {
		HttpRequestLine requestLine = httpRequest.getRequestLine();
		return handlers.stream()
			.filter(h -> h.isSupport(requestLine))
			.findFirst()
			.orElse(NotFoundHandler.getInstance());
	}
}
