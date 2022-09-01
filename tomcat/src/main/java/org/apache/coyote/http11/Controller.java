package org.apache.coyote.http11;

import java.io.IOException;

public class Controller {

	private final Response responseGenerator;
	private final UserService userService;

	public Controller(Response responseGenerator, UserService userService) {
		this.responseGenerator = responseGenerator;
		this.userService = userService;
	}

	public String run(String response, String url) throws IOException {
		if (url.equals("/")) {
			var responseBody = "Hello world!";
			return responseGenerator.createResponseFormat("Content-Type: text/html;charset=utf-8 ", responseBody);
		}

		if (url.startsWith("/index.html")) {
			return responseGenerator.createResponse(url, "text/html");
		}

		if (url.startsWith("/css")) {
			return responseGenerator.createResponse(url, "text/css");
		}

		if (url.startsWith("/js")) {
			return responseGenerator.createResponse(url, "application/javascript");
		}

		if (url.startsWith("/assets")) {
			return responseGenerator.createResponse(url, "application/javascript");
		}

		if (url.startsWith("/login")) {
			int index = url.indexOf("?");
			String path = url.substring(0, index);
			String queryString = url.substring(index + 1);
			userService.logUserInfo(queryString);
			response = responseGenerator.createResponse(path + ".html", "text/html");
		}
		return response;
	}
}
