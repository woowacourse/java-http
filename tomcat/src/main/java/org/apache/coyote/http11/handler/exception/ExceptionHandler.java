package org.apache.coyote.http11.handler.exception;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;

import java.io.IOException;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.response.HttpResponse;

public interface ExceptionHandler {

	boolean isSupported(final Exception exception);

	HttpResponse handleTo() throws IOException;

	default HttpHeaders resolveHeader(final String body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return headers;
	}
}
