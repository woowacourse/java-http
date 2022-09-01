package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

	private static final String HTTP_MESSAGE_DELIMITER = " ";
	private static final int RESOURCE_LOCATION = 1;
	private static final String STATIC_PATH = "static/";
	private static final String DEFAULT_CONTENT = "Hello world!";

	private final String contentType;
	private final String responseBody;

	public HttpResponse(final BufferedReader bufferedReader) throws IOException {
		final String recourse = parseRequestResource(bufferedReader);

		contentType = selectContentType(recourse);
		responseBody = loadResourceContent(recourse);
	}

	public String createResponseMessage() {
		return String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Type: " + contentType + ";charset=utf-8 ",
			"Content-Length: " + responseBody.getBytes().length + " ",
			"",
			responseBody);
	}

	private String parseRequestResource(final BufferedReader bufferedReader) throws IOException {
		final String firstLine = bufferedReader.readLine();

		return firstLine.split(HTTP_MESSAGE_DELIMITER)[RESOURCE_LOCATION];
	}

	private String loadResourceContent(final String resource) {
		final URL path = getClass().getClassLoader().getResource(STATIC_PATH + resource);

		final String content;
		try {
			content = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
		} catch (IOException e) {
			return DEFAULT_CONTENT;
		}
		return content;
	}

	private String selectContentType(final String resource) {
		final String extension = resource.split("\\.")[1];
		if (extension.equals("css")) {
			return "text/css";
		}
		if (extension.equals("js")) {
			return "text/javascript";
		}
		if (extension.equals("svg")) {
			return "image/svg+xml";
		}
		return "text/html";
	}
}
