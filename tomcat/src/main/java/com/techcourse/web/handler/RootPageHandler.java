package com.techcourse.web.handler;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;

public class RootPageHandler implements Handler {

	private static final RootPageHandler instance = new RootPageHandler();
	private static final String ROOT_PATH = "/";
	private static final String ROOT_PATH_CONTENT = "Hello world!";
	private static final String ROOT_PATH_CONTENT_TYPE = "text/html";

	private RootPageHandler() {
	}

	public static RootPageHandler getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequestLine requestLine) {
		HttpMethod method = requestLine.getMethod();
		String requestPath = requestLine.getRequestPath();

		return method == HttpMethod.GET && requestPath.equals(ROOT_PATH);
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		HttpResponseHeader responseHeader = new HttpResponseHeader();
		HttpResponseBody responseBody = new HttpResponseBody(ROOT_PATH_CONTENT_TYPE, ROOT_PATH_CONTENT.getBytes());

		return HttpResponse.ok(responseHeader, responseBody);
	}
}
