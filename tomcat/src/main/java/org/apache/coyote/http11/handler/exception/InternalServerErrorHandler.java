package org.apache.coyote.http11.handler.exception;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;

import java.io.IOException;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class InternalServerErrorHandler implements ExceptionHandler {

	private static final String REDIRECT_URI = "http://localhost:8080/500.html";

	@Override
	public boolean isSupported(final Exception exception) {
		return false;
	}

	@Override
	public HttpResponse handleTo() throws IOException {
		final String body = "";
		return new HttpResponse(
			HttpStatusCode.TEMPORARILY_MOVED_302,
			body,
			resolveHeader(body)
		);
	}

	private HttpHeaders resolveHeader(final String body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		headers.put(LOCATION.getValue(), REDIRECT_URI);
		return headers;
	}
}
