package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatusCode.*;

import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundController implements HttpController {

	private static final String MESSAGE = "요청하신 리소스를 찾을 수 없습니다.";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return false;
	}

	@Override
	public void handleTo(final HttpRequest request, final HttpResponse response) {
		response.setResponse(NOT_FOUND_404, MESSAGE, MimeType.HTML);
	}
}
