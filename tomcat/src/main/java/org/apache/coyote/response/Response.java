package org.apache.coyote.response;

import org.apache.coyote.Cookie;
import org.apache.coyote.MimeType;

public class Response {

	private StatusLine statusLine;
	private ResponseHeader header;
	private String responseBody;

	public Response() {
		this(null, new ResponseHeader(), null);
	}

	public Response(final StatusLine statusLine, final ResponseHeader header, final String responseBody) {
		this.statusLine = statusLine;
		this.header = header;
		this.responseBody = responseBody;
	}

	public void redirect(final String location) {
		this.statusLine = new StatusLine(StatusCode.FOUND);
		this.header.add(HeaderType.LOCATION, location);
	}

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public void setStatusCode(final StatusCode code) {
		this.statusLine = new StatusLine(code);
	}

	public ResponseHeader getHeader() {
		return header;
	}

	public void addHeader(final HeaderType type, final String value) {
		this.header.add(type, value);
	}

	public void addCookie(final Cookie cookie) {
		header.addCookie(cookie);
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(final String responseBody, final MimeType mimeType) {
		header.addContentType(mimeType);
		header.addContentLength(responseBody);
		this.responseBody = responseBody;
	}
}
