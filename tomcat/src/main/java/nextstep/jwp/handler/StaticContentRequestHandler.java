package nextstep.jwp.handler;

import org.apache.catalina.RequestHandler;
import org.apache.coyote.MimeType;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticContentRequestHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(StaticContentRequestHandler.class);
	private static final String REQUEST_PATH = "/*";

	public StaticContentRequestHandler() {
		super(REQUEST_PATH);
	}

	@Override
	public Response handle(final Request request) {
		final String requestPath = request.getPath();
		if (requestPath == null) {
			return Response.notFound();
		}
		try {
			final var responseBody = ResourceProvider.provide(requestPath);
			final var mimeType = MimeType.fromPath(requestPath);
			return Response.ok(responseBody, mimeType);
		} catch (IllegalArgumentException e) {
			log.warn("{}: {}", request.getPath(), e.getMessage());
			return Response.notFound();
		}
	}
}
