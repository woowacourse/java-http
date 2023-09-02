package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.headers.HttpHeaderType;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RootHandler implements HttpHandler {

	private final String endPoint = "/";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.getEndPoint().equals(endPoint);
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) {
		return new HttpResponse(
			HttpStatusCode.OK_200,
			resolveBody(),
			//TODO: resolveBody 수정하기
			null
		);
	}

	private String resolveBody() {
		final String body = "Hello world!";
		return String.join(System.lineSeparator(),
			"Content-Type: text/html;charset=utf-8 ",
			String.format("Content-Length: %d ", body.getBytes().length),
			"",
			body
		);
	}

	private HttpHeaders resolveHeader(final HttpRequest request, final String body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(HttpHeaderType.CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(HttpHeaderType.CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return headers;
	}
}
