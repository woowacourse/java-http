package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.headers.MimeType.*;
import static org.apache.coyote.http11.request.HttpVersion.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;

public class HttpResponse {

	private HttpStatusCode statusCode;
	private String body;
	private HttpHeaders headers = new HttpHeaders();

	public HttpResponse() {
	}

	public HttpResponse(final HttpStatusCode statusCode, final String body, final HttpHeaders headers) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = headers;
	}

	public void redirect(final String location) {
		final String body = "";
		setResponse(TEMPORARILY_MOVED_302, body, HTML);
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
		return String.join("\r\n",
			HTTP_1_1.getContent() + " " + statusCode.buildResponse(),
			headers.build(),
			body
		);
	}
}
