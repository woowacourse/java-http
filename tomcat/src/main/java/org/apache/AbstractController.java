package org.apache;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		if (request.hasMethod(HttpMethod.GET)) {
			doGet(request, response);
			return;
		}
		if (request.hasMethod(HttpMethod.POST)) {
			doPost(request, response);
			return;
		}
		HttpResponse.methodNotAllowed();
	}

	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
	}

	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
	}
}
