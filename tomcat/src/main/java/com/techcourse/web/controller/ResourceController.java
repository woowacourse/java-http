package com.techcourse.web.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;

import com.techcourse.web.Resource;
import com.techcourse.web.util.ResourceLoader;

public class ResourceController extends AbstractController {

	private static final String STATIC_FILE_EXTENSION_REGEX = ".*\\.(html|css|js|ico|svg)";
	private static final Pattern STATIC_FILE_PATTERN = Pattern.compile(STATIC_FILE_EXTENSION_REGEX);
	private static final ResourceController instance = new ResourceController();

	private ResourceController() {
	}

	public static ResourceController getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequest request) {
		HttpRequestLine requestLine = request.getRequestLine();
		HttpMethod method = requestLine.getMethod();
		String requestPath = requestLine.getRequestPath();

		return method == HttpMethod.GET && STATIC_FILE_PATTERN.matcher(requestPath).matches();
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) throws IOException {
		String requestPath = request.getRequestLine().getRequestPath();
		Resource resource = ResourceLoader.getInstance().loadResource(requestPath);

		response.setStatusCode(HttpStatusCode.OK);
		response.setBody(resource.getContentType(), resource.getContent());
	}
}
