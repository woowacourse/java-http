package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterHandler implements HttpHandler {

	private static final Map<HttpMethod, HttpHandle> HANDLE_MAP = Map.of(
		HttpMethod.GET, request -> servingStaticResource(),
		HttpMethod.POST, RegisterHandler::registerProcess
	);
	private static final String END_POINT = "/register";
	private static final String STATIC_RESOURCE_FILE_PATH = "static/register.html";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.getEndPoint().equals(END_POINT)
			&& HANDLE_MAP.containsKey(request.getHttpMethod());
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) throws IOException {
		final HttpMethod httpMethod = request.getHttpMethod();
		return HANDLE_MAP.get(httpMethod)
			.handle(request);
	}

	private static HttpResponse registerProcess(final HttpRequest request) {
		return null;
	}

	private static HttpResponse servingStaticResource() {
		try {
			final URL url = RegisterHandler.class.getClassLoader()
				.getResource(STATIC_RESOURCE_FILE_PATH);
			final String body = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
			return new HttpResponse(
				OK_200,
				body,
				resolveHeader(body)
			);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static HttpHeaders resolveHeader(final String body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return headers;
	}
}
