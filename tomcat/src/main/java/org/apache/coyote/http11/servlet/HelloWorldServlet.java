package org.apache.coyote.http11.servlet;

import static org.apache.coyote.http11.common.HeaderKey.*;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class HelloWorldServlet extends AbstractServlet {
	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {

	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
		response.setRequestLine("HTTP/1.1", HttpStatusCode.OK);
		response.setHeaders(Map.of(
			"Content-Type", "text/html;charset=utf-8",
			"Content-Length", String.valueOf(13)));
		response.setBodyByPlainText("Hello, World!");
	}
}
