package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.headers.MimeType.*;
import static org.apache.coyote.http11.request.HttpVersion.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpResponse {

	private final HttpStatusCode statusCode;
	private final String body;
	private final HttpHeaders headers;

	public HttpResponse(final HttpStatusCode statusCode, final String body, final HttpHeaders headers) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = headers;
	}

	public static HttpResponse redirect(final String location) {
		final String body = "";
		final HttpHeaders httpHeaders = HttpHeaders.of(body, HTML);
		httpHeaders.put(LOCATION.getValue(), location);
		return new HttpResponse(TEMPORARILY_MOVED_302, body, httpHeaders);
	}

	public String buildResponse() {
		return String.join("\r\n",
			HTTP_1_1.getContent() + statusCode.buildResponse(),
			headers.build(),
			body
		);
	}
}
