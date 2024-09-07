package com.techcourse.web.handler;

import java.io.IOException;

import org.apache.coyote.http11.HttpRequest;

import com.techcourse.web.HttpResponse;
import com.techcourse.web.HttpStatusCode;
import com.techcourse.web.annotation.Request;

public class PageHandler extends RequestHandler {

	private static final PageHandler instance = new PageHandler();

	private PageHandler() {
	}

	public static PageHandler getInstance() {
		return instance;
	}

	@Request(path = "*", method = "GET")
	public HttpResponse getResource(HttpRequest request) throws IOException {
		if (request.getRequestPath().contains("favicon")) {
			return HttpResponse.builder()
				.protocol(request.getProtocol())
				.statusCode(HttpStatusCode.NOT_FOUND)
				.build();
		}
		return HttpResponse.builder()
			.protocol(request.getProtocol())
			.statusCode(HttpStatusCode.OK)
			.body(getResponseBody(request.getRequestPath()))
			.build();
	}

	private HttpResponse.Body getResponseBody(String requestTarget) throws IOException {
		if (requestTarget.equals("/")) {
			return HttpResponse.Body.fromString("Hello world!");
		}
		return HttpResponse.Body.fromPath(requestTarget);
	}
}
