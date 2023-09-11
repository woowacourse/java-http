package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.headers.MimeType.*;
import static org.apache.coyote.http11.request.HttpVersion.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;

public class HttpResponse {

	private static final String EMPTY_BODY = "";

	private HttpStatusCode statusCode;
	private String body;
	private final HttpHeaders headers;

	public HttpResponse() {
		this(null, EMPTY_BODY, new HttpHeaders());
	}

	public HttpResponse(final HttpStatusCode statusCode, final String body, final HttpHeaders headers) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = headers;
	}

	public void redirect(final String location) {
		setResponse(TEMPORARILY_MOVED_302, EMPTY_BODY, HTML);
		headers.addLocation(location);
	}

	public void addSetCookie(final String setCookieValue) {
		headers.addSetCookie(setCookieValue);
	}

	public void setResponse(final HttpStatusCode code, final String body, final MimeType type) {
		this.statusCode = code;
		this.body = body;
		headers.addType(type);
		headers.addContentLength(body);
	}

	public String buildResponse() {
		if (statusCode == null) {
			throw new IllegalStateException();
		}
		return String.join("\r\n",
			HTTP_1_1.getContent() + " " + statusCode.buildResponse(),
			headers.build(),
			body
		);
	}
}
