package org.apache.coyote.http11.response;

import java.util.List;

import org.apache.coyote.http11.HttpHeaders;

public class HttpResponse2 {

	private final StatusLine statusLine;
	private final HttpHeaders httpHeaders;
	private final List<String> bodies;

	private HttpResponse2(final StatusLine statusLine, final HttpHeaders httpHeaders, final List<String> bodies) {
		this.statusLine = statusLine;
		this.httpHeaders = httpHeaders;
		this.bodies = bodies;
	}

	public String toMessage() {
		return String.join("\r\n",
			statusLine.toMessage(),
			httpHeaders.toMessage(),
			String.join("\n", bodies)
		);
	}
}
