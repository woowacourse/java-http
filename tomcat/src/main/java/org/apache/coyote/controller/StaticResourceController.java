package org.apache.coyote.controller;

import org.apache.coyote.http11.exception.FileNotFoundException;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.util.StaticResourceUtil;

public class StaticResourceController implements Controller {

	private static final String NOT_FOUND_HTML = "404.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		try {
			String responseBody = StaticResourceUtil.getContent(request.getUrl());
			response.setStatus(HttpStatus.OK);
			response.setBody(responseBody);
			response.addHeader(HttpHeader.CONTENT_TYPE, request.getContentType().value());
		} catch (FileNotFoundException exception) {
			String responseBody = StaticResourceUtil.getContent(NOT_FOUND_HTML);
			response.setStatus(HttpStatus.NOT_FOUND);
			response.setBody(responseBody);
			response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
		}
	}
}
