package org.apache;

import java.util.ArrayList;
import java.util.List;

public class HandlerMapping {

	public static List<RequestHandler> handlers = new ArrayList<>();

	public void register(RequestHandler handler) {
		handlers.add(handler);
	}

	public RequestHandler getHandler(HttpRequest request) {
		return handlers.stream()
			.filter(handler -> handler.canHandle(request))
			.findFirst()
			.orElseThrow(()-> new IllegalArgumentException("no such Request handler"));
	}
}
