package nextstep.jwp.handler;

import org.apache.catalina.RequestHandler;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public class BaseRequestHandler extends RequestHandler {

	private static final String REQUEST_PATH = "/";
	private static final String RESPONSE_BODY = "Hello world!";

	public BaseRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	protected Response doGet(final Request request) {
		return Response.ok(RESPONSE_BODY, MimeType.HTML);
	}
}
