package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.response.header.StatusCode;
import org.apache.coyote.http11.response.header.StatusLine;
import org.apache.coyote.http11.util.ResourceSearcher;

public class HttpResponse {

	private static final ResourceSearcher RESOURCE_SEARCHER = new ResourceSearcher();
	private static final String FILE_REGEX = "\\.";
	private static final int EXTENSION_LOCATION = 1;

	private final StatusLine statusLine;
	private final HttpHeaders httpHeaders;
	private final String body;

	private HttpResponse(final StatusLine statusLine, final HttpHeaders httpHeaders, final String body) {
		this.statusLine = statusLine;
		this.httpHeaders = httpHeaders;
		this.body = body;
	}

	public static HttpResponse of(final String httpVersion, final String resource) {
		final StatusLine statusLine = new StatusLine(httpVersion, StatusCode.OK);

		final String contentType = selectContentType(resource);
		final String body = loadResourceContent(resource);
		final HttpHeaders httpHeaders = createHttpHeaders(contentType, body);

		return new HttpResponse(statusLine, httpHeaders, body);
	}

	private static String selectContentType(final String resource) {
		final String[] fileElements = resource.split(FILE_REGEX);

		return ContentType.getContentType(fileElements[EXTENSION_LOCATION]);
	}

	private static String loadResourceContent(final String resource) {
		return RESOURCE_SEARCHER.loadContent(resource);
	}

	private static HttpHeaders createHttpHeaders(final String contentType, final String body) {
		int length = body.getBytes().length;
		return HttpHeaders.init()
			.add("Content-Type", contentType + ";charset=utf-8")
			.add("Content-Length", String.valueOf(length));
	}

	public String toMessage() {
		return String.join("\r\n",
			statusLine.toMessage(),
			httpHeaders.toMessage(),
			"",
			body
		);
	}
}
