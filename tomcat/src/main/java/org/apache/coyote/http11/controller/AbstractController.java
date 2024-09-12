package org.apache.coyote.http11.controller;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		Method method = request.getMethod();
		if (method.isGet()) {
			doGet(request, response);
		} else if (method.isPost()) {
			doPost(request, response);
		}
	}

	protected abstract void doPost(HttpRequest request, HttpResponse response);

	protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;
}
