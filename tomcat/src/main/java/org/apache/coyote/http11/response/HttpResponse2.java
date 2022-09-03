package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.KindOfContent;
import org.apache.coyote.http11.response.header.StatusCode;
import org.apache.coyote.http11.response.header.StatusLine;

public class HttpResponse2 {

	private final StatusLine statusLine;
	private final HttpHeaders httpHeaders;
	private final String body;

	private HttpResponse2(final StatusLine statusLine, final HttpHeaders httpHeaders, final String body) {
		this.statusLine = statusLine;
		this.httpHeaders = httpHeaders;
		this.body = body;
	}

	public static HttpResponse2 of(final String httpVersion, final KindOfContent kindOfContent, final String body) {
		final StatusLine statusLine = new StatusLine(httpVersion, StatusCode.OK);
		final HttpHeaders httpHeaders = createHttpHeaders(kindOfContent, body);

		return new HttpResponse2(statusLine, httpHeaders, body);
	}

	private static HttpHeaders createHttpHeaders(final KindOfContent kindOfContent, final String body) {
		return HttpHeaders.init()
			.add("Content-Type", kindOfContent.name() + ";charset=utf-8 ")
			.add("Content-Length", String.valueOf(body.getBytes().length));
	}

	public String toMessage() {
		return String.join("\r\n",
			statusLine.toMessage(),
			httpHeaders.toMessage(),
			body
		);
	}
}
