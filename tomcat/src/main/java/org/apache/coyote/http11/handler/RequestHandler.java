package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public interface RequestHandler {

	boolean canHandle(Request request);

	Response handle(Request request);
}
