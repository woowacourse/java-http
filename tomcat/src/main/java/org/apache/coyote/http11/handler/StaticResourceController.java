package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URL;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.StaticResourceResolver;

public class StaticResourceController implements HttpController {

	private static final String RESOURCE_PREFIX = "static";

	@Override
	public boolean isSupported(final HttpRequest request) {
		final URL resourceUrl = extractURL(request);
		return resourceUrl != null;
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) throws IOException {
		final URL url = extractURL(request);
		return StaticResourceResolver.resolve(url);
	}

	private URL extractURL(final HttpRequest request) {
		return getClass().getClassLoader()
			.getResource(RESOURCE_PREFIX.concat(request.getPath()));
	}
}
