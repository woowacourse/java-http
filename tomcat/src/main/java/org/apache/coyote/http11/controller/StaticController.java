package org.apache.coyote.http11.controller;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticController extends AbstractController {
	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
		response.setVersionOfProtocol("HTTP/1.1");
		response.setStatusCode(200);
		response.setStatusMessage("OK");
		response.setBody("static" + request.getPath().value());
	}
}
