package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.handler.ResourceProvider;

public class Response {

	private static final String CRLF = "\r\n";
	private static final String HEADER_BODY_SEPARATOR = "";

	private final StatusLine statusLine;
	private final ResponseHeader header;
	private final String responseBody;

	public Response(final StatusLine statusLine, final ResponseHeader header, final String responseBody) {
		this.statusLine = statusLine;
		this.header = header;
		this.responseBody = responseBody;
	}

	public static Response ok(final String responseBody, final MimeType mimeType) {
		return of(StatusCode.OK, responseBody, mimeType);
	}

	public static Response unauthorized() {
		return code(StatusCode.UNAUTHORIZED);
	}

	public static Response notFound() {
		return code(StatusCode.NOT_FOUND);
	}

	public static Response redirectWithCookie(final String location, final HttpCookies cookies) {
		final var statusLine = new StatusLine(StatusCode.FOUND);
		final var header = getHeader();
		header.add(ResponseHeaderType.LOCATION, location);
		header.addCookies(cookies);
		return new Response(statusLine, header, null);
	}

	public static Response redirect(final String location) {
		final var statusLine = new StatusLine(StatusCode.FOUND);
		final var header = getHeader();
		header.add(ResponseHeaderType.LOCATION, location);
		return new Response(statusLine, header, null);
	}

	private static Response code(final StatusCode code) {
		return of(code, ResourceProvider.provide(code.getResourcePath()), MimeType.HTML);
	}

	public static Response of(final StatusCode code, final String responseBody, final MimeType mimeType) {
		final var statusLine = new StatusLine(code);
		final var header = getHeader(responseBody, mimeType);
		return new Response(statusLine, header, responseBody);
	}

	private static ResponseHeader getHeader() {
		return getHeader("", MimeType.HTML);
	}

	private static ResponseHeader getHeader(final String responseBody, final MimeType mimeType) {
		final Map<ResponseHeaderType, String> headers = new HashMap<>();
		headers.put(ResponseHeaderType.CONTENT_TYPE, mimeType.formatMimeType());
		headers.put(ResponseHeaderType.CONTENT_LENGTH, Integer.toString(responseBody.getBytes().length));
		return new ResponseHeader(headers);
	}

	public String format() {
		return String.join(CRLF,
			statusLine.formatStatusLine(),
			header.formatHeader(),
			HEADER_BODY_SEPARATOR,
			responseBody);
	}
}
