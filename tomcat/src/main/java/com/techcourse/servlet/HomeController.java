package com.techcourse.servlet;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import jakarta.servlet.AbstractController;

public class HomeController extends AbstractController {

	private static final String URI_PATTERN = "/";
	public static final String HOME_PAGE_CONTENT = "Hello world!";

	@Override
	public boolean canHandle(HttpRequest request) {
		return (request.hasMethod(HttpMethod.GET) && URI_PATTERN.equals(request.getUri()));
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
		response.ok(HOME_PAGE_CONTENT.getBytes());
	}

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
		throw new IllegalArgumentException("cannot request post request to HomeController");
	}
}
