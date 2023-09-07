package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.HttpStatusCode.*;

import org.apache.coyote.http11.handler.HttpController;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RootController implements HttpController {

	private static final String END_POINT = "/";
	private static final String CONSTANT_BODY = "Hello world!";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.equalPath(END_POINT);
	}

	@Override
	public void handleTo(final HttpRequest request, final HttpResponse response) {
		response
			.setResponse(OK_200, CONSTANT_BODY, MimeType.HTML);
	}
}
