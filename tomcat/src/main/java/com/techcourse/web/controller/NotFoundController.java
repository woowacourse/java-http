package com.techcourse.web.controller;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;

import com.techcourse.web.Resource;
import com.techcourse.web.util.ResourceLoader;

public class NotFoundController extends AbstractController {

	private static final NotFoundController instance = new NotFoundController();

	private NotFoundController() {
	}

	public static NotFoundController getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequest request) {
		return false;
	}

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		Resource resource = ResourceLoader.getInstance().loadResource("/404.html");
		response.setStatusCode(HttpStatusCode.NOT_FOUND);
		response.setBody(resource.getContentType(), resource.getContent());
	}
}
