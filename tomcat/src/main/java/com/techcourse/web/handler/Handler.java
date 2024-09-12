package com.techcourse.web.handler;

import java.io.IOException;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;

public interface Handler {

	boolean isSupport(HttpRequestLine requestLine);

	HttpResponse handle(HttpRequest request) throws IOException;
}
