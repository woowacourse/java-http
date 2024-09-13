package org.apache.coyote.http11.servlet;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class StaticServlet extends AbstractServlet {
	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
		response.setRequestLine("HTTP/1.1", HttpStatusCode.OK);
		response.setBody("static" + request.getPath().value());
	}
}
