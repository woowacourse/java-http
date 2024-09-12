package org.apache.coyote.http11.servlet;

import java.io.IOException;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Servlet {

	private final HandlerMapping handlerMapping;

	public Servlet() {
		this.handlerMapping = new HandlerMapping();
	}

	public void doDispatch(HttpRequest request, HttpResponse response) throws IOException {
		Controller controller = handlerMapping.getController(request);

		controller.service(request, response);
	}
}
