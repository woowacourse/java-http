package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.ContentType.UTF;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.common.StatusCode.OK;
import static org.apache.coyote.http11.common.Version.HTTP_1_1;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpHeader;
import org.apache.coyote.http11.response.HttpHeaders;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusLine;

public class RootController implements Handler {
	@Override
	public HttpResponse handle(HttpRequest httpRequest) {

		final HttpStatusLine httpStatusLine = HttpStatusLine.of(HTTP_1_1, OK);
		final String responseBody = "Hello world!";
		final HttpHeaders httpHeaders = HttpHeaders.from(
			HttpHeader.of(CONTENT_TYPE, HTML.getValue(), UTF.getValue()),
			HttpHeader.of(CONTENT_LENGTH, String.valueOf(responseBody.length())));

		return HttpResponse.of(httpStatusLine, httpHeaders, responseBody);
	}
}
