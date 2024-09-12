package com.techcourse.web.handler;

import java.io.IOException;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;

import com.techcourse.web.util.ResourceLoader;

public interface Handler {

	boolean isSupport(HttpRequestLine requestLine);

	HttpResponse handle(HttpRequest request) throws IOException;

	default HttpResponse notFoundResponse() throws IOException {
		HttpResponseBody notFoundPage = ResourceLoader.getInstance().loadResource("/404.html");
		return HttpResponse.notFound(new HttpResponseHeader(), notFoundPage);
	}

	default HttpResponse redirect(HttpResponseHeader header, String location) {
		header.addHeader("Location", location);
		return HttpResponse.redirect(header);
	}
}
