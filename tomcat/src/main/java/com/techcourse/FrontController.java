package com.techcourse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FrontController {

	private final Controller controller;

	public FrontController() {
		this.controller = new Controller();
	}

	public String processGetRequest(String uri) throws IOException {
		if (uri.equals("/")) {
			var responseBody = controller.getHomePage();
			var response = "HTTP/1.1 200 OK \r\n";
			response += String.join("\r\n", getResponseHeaders(uri, responseBody, "GET"));
			response += "\r\n" + "\r\n" + responseBody;
			return response;
		}
		if (uri.equals("/register")) {
			var responseBody = controller.getRegisterPage();
			var response = "HTTP/1.1 200 OK \r\n";
			response += String.join("\r\n", getResponseHeaders(uri, responseBody, "GET"));
			response += "\r\n" + "\r\n" + responseBody;
			return response;
		}
		var responseBody = controller.getUriPage(uri);
		var response = "HTTP/1.1 200 OK \r\n";
		response += String.join("\r\n", getResponseHeaders(uri, responseBody, "GET"));
		response += "\r\n" + "\r\n" + responseBody;
		return response;
	}

	public String processPostRequest(String uri, Map<String, String> params) {
		if (uri.startsWith("/login")) {
			var redirectUri = "/401.html";
			UUID uuid;
			try {
				uuid = controller.login(params.get("account"), params.get("password"));
				redirectUri = "/index.html";

				var response = "HTTP/1.1 302 Found \r\n";
				response += String.join("\r\n", getResponseHeaders(uri, redirectUri, "302"));
				response += "\r\n" + "Set-Cookie: JSESSIONID=" + uuid;
				response += "\r\n" + "\r\n" + redirectUri;
				return response;
			} catch (RuntimeException exception) {
				var response = "HTTP/1.1 302 Found \r\n";
				response += String.join("\r\n", getResponseHeaders(uri, redirectUri, "302"));
				response += "\r\n" + "\r\n" + redirectUri;
				return response;
			}
		}

		if (uri.startsWith("/register")) {
			var redirectUri = "/401.html";

			boolean isSucceed = controller.register(params.get("account"), params.get("password"), params.get("email"));
			if (isSucceed) {
				redirectUri = "/index.html";
			}

			var response = "HTTP/1.1 302 Found \r\n";
			response += String.join("\r\n", getResponseHeaders(uri, redirectUri, "302"));
			response += "\r\n" + "\r\n" + redirectUri;
			return response;
		}

		throw new IllegalArgumentException("undefined POST URI");
	}

	private List<String> getResponseHeaders(String uri, String responseBody, String status) {
		List<String> headers = new ArrayList<>();
		headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
		headers.add("Content-Length: " + calculateContentLength(responseBody) + " ");

		if (status.equals("302")) {
			headers.add("Location: " + responseBody);
		}
		return headers;
	}

	private int calculateContentLength(String content) {
		return content.replaceAll("\r\n", "\n").getBytes(StandardCharsets.UTF_8).length;
	}

	private String getContentType(String uri) {
		if (uri.startsWith("/css")) {
			return "text/css";
		}
		return "text/html";
	}
}
