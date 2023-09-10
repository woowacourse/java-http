package nextstep.jwp.handler;

import static org.apache.coyote.response.StatusCode.*;

import org.apache.catalina.AbstractHandler;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticContentRequestHandler extends AbstractHandler {

	private static final Logger log = LoggerFactory.getLogger(StaticContentRequestHandler.class);
	private static final String REQUEST_PATH = "/*";

	public StaticContentRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	protected void doGet(final Request request, final Response response) {
		final String requestPath = request.getPath();
		if (requestPath == null) {
			response.redirect(NOT_FOUND.getResourcePath());
			return;
		}
		try {
			final var responseBody = ResourceProvider.provide(requestPath);
			final var mimeType = MimeType.fromPath(requestPath);
			response.setStatusCode(OK);
			response.setResponseBody(responseBody, mimeType);
		} catch (IllegalArgumentException e) {
			log.warn("{}: {}", request.getPath(), e.getMessage());
			response.redirect(NOT_FOUND.getResourcePath());
		}
	}

}
