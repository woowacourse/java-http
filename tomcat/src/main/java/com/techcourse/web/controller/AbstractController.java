package com.techcourse.web.controller;

import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;

public abstract class AbstractController implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		HttpRequestLine requestLine = request.getRequestLine();
		HttpMethod method = requestLine.getMethod();

		if (method == HttpMethod.GET) {
			doGet(request, response);
			return;
		}
		if (method == HttpMethod.POST) {
			doPost(request, response);
			return;
		}

		badRequest(response);
	}

	public abstract boolean isSupport(HttpRequest request);

	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
	}

	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
	}

	protected void redirect(HttpResponse response, String location) {
		response.setStatusCode(HttpStatusCode.FOUND);
		response.addHeader(HttpHeader.LOCATION.getName(), location);
	}

	private void badRequest(HttpResponse response) {
		response.setStatusCode(HttpStatusCode.BAD_REQUEST);
	}
}
