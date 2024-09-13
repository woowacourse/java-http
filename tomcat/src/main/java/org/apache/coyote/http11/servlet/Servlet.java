package org.apache.coyote.http11.servlet;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Servlet {
	void service(HttpRequest request, HttpResponse response) throws IOException;
}
