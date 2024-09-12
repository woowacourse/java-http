package org.apache;

import java.io.IOException;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomeRequestHandler implements RequestHandler {

	private static final String URI_PATTERN = "/";

	@Override
	public boolean canHandle(HttpRequest request) {
		return (request.hasMethod(HttpMethod.GET) && URI_PATTERN.equals(request.getUri()));
	}

	@Override
	public HttpResponse handle(HttpRequest httpRequest) throws IOException {
		return HttpResponse.ok(httpRequest.getUri(), "Hello world!");
	}
}
