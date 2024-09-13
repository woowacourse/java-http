package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class AbstractServlet implements Servlet {
	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		Method method = request.getMethod();
		if (method.isGet()) {
			doGet(request, response);
		} else if (method.isPost()) {
			doPost(request, response);
		} else {
			response.setRequestLine("HTTP/1.1", HttpStatusCode.METHOD_NOT_ALLOWED);
			response.setHeaders(Map.of("Allow", "GET, POST"));
		}
	}

	protected abstract void doPost(HttpRequest request, HttpResponse response);

	protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;
}
