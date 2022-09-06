package org.apache.coyote.controller;

import org.apache.coyote.http11.exception.FileNotFoundException;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.header.HttpHeader;
import org.apache.coyote.http11.util.StaticResourceUtil;

public class StaticResourceController extends AbstractController {

	private static final String NOT_FOUND_HTML = "404.html";

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		try {
			String responseBody = StaticResourceUtil.getContent(request.getUrl());
			response.setStatus(HttpStatus.OK);
			response.setBody(responseBody);
			response.addHeader(HttpHeader.CONTENT_TYPE, request.getContentType().value());
		} catch (FileNotFoundException exception) {
			handleHtml(HttpStatus.NOT_FOUND, NOT_FOUND_HTML, response);
		}
	}
}
