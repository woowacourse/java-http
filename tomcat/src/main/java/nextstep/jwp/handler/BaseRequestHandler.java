package nextstep.jwp.handler;

import org.apache.catalina.AbstractHandler;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StatusCode;

public class BaseRequestHandler extends AbstractHandler {

	private static final String REQUEST_PATH = "/";
	private static final String RESPONSE_BODY = "Hello world!";

	public BaseRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	protected void doGet(final Request request, final Response response) {
		response.setStatusCode(StatusCode.OK);
		response.setResponseBody(RESPONSE_BODY, MimeType.HTML);
	}
}
