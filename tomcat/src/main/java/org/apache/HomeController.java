package org.apache;

import java.io.IOException;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StatusLine;

public class HomeController extends AbstractController {

	private static final String URI_PATTERN = "/";
	public static final String HOME_PAGE_CONTENT = "Hello world!";

	@Override
	public boolean canHandle(HttpRequest request) {
		return (request.hasMethod(HttpMethod.GET) && URI_PATTERN.equals(request.getUri()));
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
		super.doGet(request, response);
		response.setStatusLine(StatusLine.from(HttpStatus.OK));
		response.setContentType(URI_PATTERN);
		response.setResponseBody(HOME_PAGE_CONTENT.getBytes());
		response.setContentLength();
	}

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
		throw new IllegalArgumentException("cannot request post request to HomeController");
	}
}
