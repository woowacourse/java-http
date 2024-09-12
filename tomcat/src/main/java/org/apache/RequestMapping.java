package org.apache;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

	public static List<Controller> handlers = new ArrayList<>();

	public void register(Controller handler) {
		handlers.add(handler);
	}

	public Controller getController(HttpRequest request) {
		return handlers.stream()
			.filter(handler -> handler.canHandle(request))
			.findFirst()
			.orElseThrow(()-> new IllegalArgumentException("no such Request handler"));
	}
}
