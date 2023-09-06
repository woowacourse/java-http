package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.headers.MimeType.*;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RootHandler implements HttpHandler {

	private static final String END_POINT = "/";
	private static final String CONSTANT_BODY = "Hello world!";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.equalPath(END_POINT);
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) {
		return new HttpResponse(
			HttpStatusCode.OK_200,
			CONSTANT_BODY,
			HttpHeaders.of(CONSTANT_BODY, HTML)
		);
	}
}
