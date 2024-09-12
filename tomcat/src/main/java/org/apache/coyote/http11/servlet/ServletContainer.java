package org.apache.coyote.http11.servlet;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ServletContainer {

	public void invoke(HttpRequest request, HttpResponse response) throws IOException {
		Servlet servlet = new Servlet();

		servlet.doDispatch(request, response);
	}
}
