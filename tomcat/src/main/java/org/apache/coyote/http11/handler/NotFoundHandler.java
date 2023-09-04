package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class NotFoundHandler implements HttpHandler {

	@Override
	public boolean isSupported(final HttpRequest request) {
		return false;
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) {
		return new HttpResponse(
			HttpStatusCode.NOT_FOUND_404,
			"요청하신 리소스를 찾을 수 없습니다.",
			new HttpHeaders()
		);
	}
}
