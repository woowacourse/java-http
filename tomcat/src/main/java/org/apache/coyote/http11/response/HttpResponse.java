package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.MimeType;

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

	public static HttpResponse of(final StatusCode code, final String responseBody, final MimeType mimeType) {
		final var statusLine = new HttpStatusLine(code);
		final var header = getHeader(responseBody, mimeType);
		return new HttpResponse(statusLine, header, responseBody);
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
