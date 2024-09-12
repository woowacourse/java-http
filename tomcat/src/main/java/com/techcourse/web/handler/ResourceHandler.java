package com.techcourse.web.handler;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;

import com.techcourse.web.util.ResourceLoader;

public class ResourceHandler implements Handler {

	private static final ResourceHandler instance = new ResourceHandler();
	private static final String STATIC_FILE_EXTENSION_REGEX = ".*\\.(html|css|js|ico|svg)";
	private static final Pattern STATIC_FILE_PATTERN = Pattern.compile(STATIC_FILE_EXTENSION_REGEX);

	private ResourceHandler() {
	}

	public static ResourceHandler getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequestLine requestLine) {
		HttpMethod method = requestLine.getMethod();
		String requestPath = requestLine.getRequestPath();

		return method == HttpMethod.GET && STATIC_FILE_PATTERN.matcher(requestPath).matches();
	}

	@Override
	public HttpResponse handle(HttpRequest request) throws IOException {
		HttpResponseHeader responseHeader = new HttpResponseHeader();
		String requestPath = request.getRequestLine().getRequestPath();
		HttpResponseBody responseBody = ResourceLoader.getInstance().loadResource(requestPath);

		return HttpResponse.ok(responseHeader, responseBody);
	}
}
