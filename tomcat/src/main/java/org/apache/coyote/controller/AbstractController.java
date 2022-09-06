package org.apache.coyote.controller;

import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.header.ContentType;
import org.apache.coyote.http11.http.header.HttpHeader;
import org.apache.coyote.http11.util.StaticResourceUtil;

public abstract class AbstractController implements Controller {

	private static final String REDIRECT_URL = "/index.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		String method = request.getMethod();
		if (HttpMethod.GET.equals(method)) {
			doGet(request, response);
		}
		if (HttpMethod.POST.equals(method)) {
			doPost(request, response);
		}
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
	}

	protected void handleRedirect(HttpResponse response) {
		response.setStatus(HttpStatus.FOUND);
		response.addHeader(HttpHeader.LOCATION, REDIRECT_URL);
	}

	protected void handleHtml(HttpStatus status, String html, HttpResponse response) {
		response.setStatus(status);
		response.setBody(StaticResourceUtil.getContent(html));
		response.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
	}
}
