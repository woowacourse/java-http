package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceHandler implements HttpHandler {

	private static final String RESOURCE_PREFIX = "static";

	@Override
	public boolean isSupported(final HttpRequest request) {
		final URL resourceUrl = extractURL(request);
		return resourceUrl != null;
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) throws IOException {
		final String body = resolveBody(request);
		return new HttpResponse(
			OK_200,
			body,
			resolveHeader(request, body)
		);
	}

	private String resolveBody(final HttpRequest request) throws IOException {
		final URL url = extractURL(request);
		return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
	}

	private HttpHeaders resolveHeader(final HttpRequest request, final String body) {
		final MimeType mimeType = MimeType.parseEndpoint(request.getPath());
		final HttpHeaders headers = new HttpHeaders();
		headers.put(CONTENT_TYPE.getValue(), mimeType.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return headers;
	}

	private URL extractURL(final HttpRequest request) {
		return getClass().getClassLoader()
			.getResource(RESOURCE_PREFIX.concat(request.getPath()));
	}
}
