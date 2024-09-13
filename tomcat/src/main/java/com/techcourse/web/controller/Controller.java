package com.techcourse.web.controller;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public interface Controller {

	void service(HttpRequest request, HttpResponse response) throws Exception;

	boolean isSupport(HttpRequest request);
}
