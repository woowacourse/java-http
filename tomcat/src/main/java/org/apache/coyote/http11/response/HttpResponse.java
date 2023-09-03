package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.handler.ResourceProvider;

public class HttpResponse {

	private static final String CRLF = "\r\n";
	private static final String HEADER_BODY_SEPARATOR = "";

	private final HttpStatusLine statusLine;
	private final HttpResponseHeader header;
	private final String responseBody;

	public HttpResponse(final HttpStatusLine statusLine, final HttpResponseHeader header, final String responseBody) {
		this.statusLine = statusLine;
		this.header = header;
		this.responseBody = responseBody;
	}

	public static HttpResponse ok(final String responseBody, final MimeType mimeType) {
		return of(StatusCode.OK, responseBody, mimeType);
	}

	public static HttpResponse unauthorized() {
		return code(StatusCode.UNAUTHORIZED);
	}

	public static HttpResponse notFound() {
		return code(StatusCode.NOT_FOUND);
	}

	public static HttpResponse redirect(final String location) {
		final var statusLine = new HttpStatusLine(StatusCode.FOUND);
		final var header = getHeader();
		header.add(HttpResponseHeaderType.LOCATION, location);
		return new HttpResponse(statusLine, header, null);
	}

	private static HttpResponse code(final StatusCode code) {
		return of(code, ResourceProvider.provide(code.getResourcePath()), MimeType.HTML);
	}

	public static HttpResponse of(final StatusCode code, final String responseBody, final MimeType mimeType) {
		final var statusLine = new HttpStatusLine(code);
		final var header = getHeader(responseBody, mimeType);
		return new HttpResponse(statusLine, header, responseBody);
	}

	private static HttpResponseHeader getHeader() {
		return getHeader("", MimeType.HTML);
	}

	private static HttpResponseHeader getHeader(final String responseBody, final MimeType mimeType) {
		final Map<HttpResponseHeaderType, String> headers = new HashMap<>();
		headers.put(HttpResponseHeaderType.CONTENT_TYPE, mimeType.formatMimeType());
		headers.put(HttpResponseHeaderType.CONTENT_LENGTH, Integer.toString(responseBody.getBytes().length));
		return new HttpResponseHeader(headers);
	}

	public String format() {
		return String.join(CRLF,
			statusLine.formatStatusLine(),
			header.formatHeader(),
			HEADER_BODY_SEPARATOR,
			responseBody);
	}
}
