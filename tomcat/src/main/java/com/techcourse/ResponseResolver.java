package com.techcourse;

import com.techcourse.exception.UncheckedServletException;
import org.apache.HttpRequest;
import org.apache.HttpResponse;
import org.apache.catalina.session.SessionManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResponseResolver {

	private final Controller controller;
	private final SessionManager sessionManager;


	public ResponseResolver() {
		this.controller = new Controller();
		this.sessionManager = SessionManager.getInstance();
	}

	public HttpResponse processRequest(HttpRequest httpRequest) throws IOException {
		if (httpRequest.getMethod().equals("GET")) {
			return processGetRequest(httpRequest.getUri());
		}
		if (httpRequest.getMethod().equals("POST")) {
			return processPostRequest(httpRequest.getUri(), httpRequest.getPayload());
		}
		throw new IllegalArgumentException("unexpected http method");
	}

	private HttpResponse processGetRequest(String uri) throws IOException {
		if (uri.equals("/")) {
			var responseBody = controller.getHomePage();
			return HttpResponse.ok(uri, responseBody);
		}
		if (uri.equals("/register")) {
			var responseBody = controller.getRegisterPage();
			return HttpResponse.ok(uri, responseBody);
		}
		var responseBody = controller.getUriPage(uri);
		return HttpResponse.ok(uri, responseBody);
	}

	private HttpResponse processPostRequest(String uri, Map<String, String> params) {
		if (uri.startsWith("/login")) {
			var redirectUri = "/401.html";
			UUID uuid;
			try {
				uuid = controller.login(params.get("account"), params.get("password"));
				redirectUri = "/index.html";

				return HttpResponse.redirect(uri, redirectUri);
			} catch (RuntimeException exception) {
				return HttpResponse.redirect(uri, redirectUri);
			}
		}

		if (uri.startsWith("/register")) {
			var redirectUri = "/401.html";

			boolean isSucceed = controller.register(params.get("account"), params.get("password"), params.get("email"));
			if (isSucceed) {
				redirectUri = "/index.html";
			}

			return HttpResponse.redirect(uri, redirectUri);
		}

		throw new IllegalArgumentException("undefined POST URI");
	}
}
