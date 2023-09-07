package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HttpHandle {

	void handle(final HttpRequest request, final HttpResponse httpResponse);
}
