package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HttpHandler {

	boolean isSupported(final HttpRequest request);

	HttpResponse handleTo(final HttpRequest request);
}
