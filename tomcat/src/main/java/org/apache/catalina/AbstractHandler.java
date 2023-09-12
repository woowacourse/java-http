package org.apache.catalina;

import static org.apache.coyote.response.StatusCode.*;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public abstract class AbstractHandler implements Handler {

	private final String requestPath;

	protected AbstractHandler(final String requestPath) {
		this.requestPath = requestPath;
	}

	@Override
	public void service(final Request request, final Response response) {
		if (request.hasMethod(HttpMethod.GET)) {
			doGet(request, response);
			return;
		}
		if (request.hasMethod(HttpMethod.POST)) {
			doPost(request, response);
			return;
		}
		response.redirect(NOT_FOUND.getResourcePath());
	}

	protected void doGet(final Request request, final Response response) {
		response.redirect(NOT_FOUND.getResourcePath());
	}

	protected void doPost(final Request request, final Response response) {
		response.redirect(NOT_FOUND.getResourcePath());
	}

	public String getRequestPath() {
		return requestPath;
	}
}
