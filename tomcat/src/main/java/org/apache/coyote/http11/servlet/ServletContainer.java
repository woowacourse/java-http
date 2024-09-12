package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;

public class ServletContainer {

	private final Map<Path, Servlet> servlets;

	public ServletContainer() {
		this.servlets = Map.of(
			new Path("/login"), new LoginServlet(),
			new Path("/register"), new RegisterServlet());
	}

	public void invoke(HttpRequest request, HttpResponse response) throws IOException {
		Servlet servlet = servlets.getOrDefault(request.getPath(), new StaticServlet());

		servlet.service(request, response);
	}
}
