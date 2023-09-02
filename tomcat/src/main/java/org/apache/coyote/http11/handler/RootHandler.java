package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.headers.HttpHeaderType;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RootHandler implements HttpHandler {

	private static final String END_POINT = "/";
	private static final String CONSTANT_BODY = "Hello world!";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.getEndPoint().equals(END_POINT);
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) {
		return new HttpResponse(
			HttpStatusCode.OK_200,
			CONSTANT_BODY,
			resolveHeader()
		);
	}

	private HttpHeaders resolveHeader() {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(HttpHeaderType.CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(HttpHeaderType.CONTENT_LENGTH.getValue(), String.valueOf(CONSTANT_BODY.getBytes().length));
		return headers;
	}
}
