package org.apache.coyote.controller;

import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public abstract class AbstractController implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		String method = request.getMethod();
		if (HttpMethod.GET.equals(method)) {
			doGet(request, response);
		}
		if (HttpMethod.POST.equals(method)) {
			doPost(request, response);
		}
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) throws Exception {
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) throws Exception {
	}
}
