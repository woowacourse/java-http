package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public abstract class RequestHandler {

	private final String requestPath;

	public RequestHandler() {
		this("");
	}

	protected RequestHandler(final String requestPath) {
		this.requestPath = requestPath;
	}

	public boolean canHandle(Request request) {
		return request.hasPath(requestPath);
	}

	public Response handle(final Request request) {
		if (request.hasMethod(HttpMethod.GET)) {
			return doGet(request);
		} else if (request.hasMethod(HttpMethod.POST)) {
			return doPost(request);
		}
		return Response.notFound();
	}

	protected Response doGet(final Request request) {
		return Response.notFound();
	}

	protected Response doPost(final Request request) {
		return Response.notFound();
	}
}
