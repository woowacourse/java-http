package org.apache;

import java.io.IOException;

public class HomeRequestHandler implements RequestHandler {

	private static final String URI_PATTERN = "/";

	@Override
	public boolean canHandle(HttpRequest request) {
		return (request.getMethod().equals("GET") && URI_PATTERN.equals(request.getUri()));
	}

	@Override
	public HttpResponse handle(HttpRequest httpRequest) throws IOException {
		return HttpResponse.ok(httpRequest.getUri(), "Hello world!");
	}
}
