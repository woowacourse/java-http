package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.Cookie;
import org.apache.coyote.MimeType;

import nextstep.jwp.handler.ResourceProvider;

public class Response {

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

	public static Response badRequest() {
		return code(StatusCode.BAD_REQUEST);
	}

	public static Response unauthorized() {
		return code(StatusCode.UNAUTHORIZED);
	}

	public static Response notFound() {
		return code(StatusCode.NOT_FOUND);
	}

	public static Response redirect(final String location) {
		final var statusLine = new StatusLine(StatusCode.FOUND);
		final var header = makeHeader();
		header.add(HeaderType.LOCATION, location);
		return new Response(statusLine, header, null);
	}

	private static ResponseHeader makeHeader() {
		return makeHeader("", MimeType.HTML);
	}

	private static ResponseHeader makeHeader(final String responseBody, final MimeType mimeType) {
		final Map<HeaderType, String> headers = new HashMap<>();
		headers.put(HeaderType.CONTENT_TYPE, mimeType.getValue());
		headers.put(HeaderType.CONTENT_LENGTH, Integer.toString(responseBody.getBytes().length));
		return new ResponseHeader(headers);
	}

	private static Response code(final StatusCode code) {
		final var resourcePath = code.getResourcePath();
		if (resourcePath.isEmpty()) {
			return of(code, resourcePath, MimeType.HTML);
		}
		return of(code, ResourceProvider.provide(resourcePath), MimeType.HTML);
	}

	private static Response of(final StatusCode code, final String responseBody, final MimeType mimeType) {
		final var statusLine = new StatusLine(code);
		final var header = makeHeader(responseBody, mimeType);
		return new Response(statusLine, header, responseBody);
	}

	public Response addCookie(final Cookie cookie) {
		header.addCookie(cookie);
		return this;
	}

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public ResponseHeader getHeader() {
		return header;
	}

	public String getResponseBody() {
		return responseBody;
	}
}
