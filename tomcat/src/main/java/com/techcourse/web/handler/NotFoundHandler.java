package com.techcourse.web.handler;

import java.io.IOException;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;

import com.techcourse.web.util.ResourceLoader;

public class NotFoundHandler implements Handler {

	private static final NotFoundHandler instance = new NotFoundHandler();

	private NotFoundHandler() {
	}

	public static NotFoundHandler getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequestLine requestLine) {
		return true;
	}

	@Override
	public HttpResponse handle(HttpRequest request) throws IOException {
		HttpResponseBody notFoundPage = ResourceLoader.getInstance().loadResource("/404.html");
		return HttpResponse.notFound(new HttpResponseHeader(), notFoundPage);
	}
}
