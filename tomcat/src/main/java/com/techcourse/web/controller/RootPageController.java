package com.techcourse.web.controller;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;

import com.techcourse.web.Resource;

public class RootPageController extends AbstractController {

	private static final String ROOT_PATH = "/";
	private static final String ROOT_PATH_CONTENT = "Hello world!";
	private static final String ROOT_PATH_CONTENT_TYPE = "text/html";
	private static final RootPageController instance = new RootPageController();

	private RootPageController() {
	}

	public static RootPageController getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequest request) {
		HttpRequestLine requestLine = request.getRequestLine();
		return requestLine.getRequestPath().equals(ROOT_PATH) && requestLine.getMethod() == HttpMethod.GET;
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		Resource resource = new Resource(ROOT_PATH_CONTENT_TYPE, ROOT_PATH_CONTENT.getBytes());

		response.setStatusCode(HttpStatusCode.OK);
		response.setBody(resource.getContentType(), resource.getContent());
	}
}
