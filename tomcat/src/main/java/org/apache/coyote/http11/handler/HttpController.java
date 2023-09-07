package org.apache.coyote.http11.handler;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HttpController {

	boolean isSupported(final HttpRequest request);

	void handleTo(final HttpRequest request, final HttpResponse response) throws IOException;
}
