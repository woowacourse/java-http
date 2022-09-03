package org.apache.coyote.http11.response;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.KindOfContent;
import org.apache.coyote.http11.UrlParser;
import org.apache.coyote.http11.response.header.StatusCode;

public class HttpResponse {

	private static final String HTTP_MESSAGE_DELIMITER = " ";
	private static final int RESOURCE_LOCATION = 1;
	private static final String STATIC_PATH = "static";
	private static final String DEFAULT_CONTENT = "Hello world!";
	private static final int EXTENSION_LOCATION = 1;
	private static final String FILE_REGEX = "\\.";
	private static final String ROOT_URI = "/";

	private final UrlParser urlParser;
	private final String contentType;
	private final String body;

	private HttpResponse(final UrlParser urlParser, final String contentType, final String body) {
		this.urlParser = urlParser;
		this.contentType = contentType;
		this.body = body;
	}

	public static HttpResponse from(final BufferedReader bufferedReader) throws IOException {
		final UrlParser urlParser = parseRequestResource(bufferedReader);
		final String contentType = selectContentType(urlParser.getResource());
		final String responseBody = loadResourceContent(urlParser.getResource());

		return new HttpResponse(urlParser, contentType, responseBody);
	}

	public String createResponseMessage() {
		return String.join("\r\n",
			"HTTP/1.1 " + StatusCode.OK.toMessage() + " ",
			"Content-Type: " + contentType + ";charset=utf-8 ",
			"Content-Length: " + body.getBytes().length + " ",
			"",
			body);
	}

	private static UrlParser parseRequestResource(final BufferedReader bufferedReader) throws IOException {
		final String firstLine = bufferedReader.readLine();
		final String uri = firstLine.split(HTTP_MESSAGE_DELIMITER)[RESOURCE_LOCATION];

		return new UrlParser(uri);
	}

	private static String loadResourceContent(final String resource) {
		final URL path = HttpResponse.class.getClassLoader().getResource(STATIC_PATH + resource);

		final String content;
		try {
			content = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
		} catch (IOException e) {
			return DEFAULT_CONTENT;
		}
		return content;
	}

	private static String selectContentType(final String resource) {
		if (resource.equals(ROOT_URI)) {
			return KindOfContent.getDefaultContentType();
		}
		final String[] fileElements = resource.split(FILE_REGEX);
		return KindOfContent.getContentType(fileElements[EXTENSION_LOCATION]);
	}

	public Map<String, String> getQueries() {
		return urlParser.getQueries();
	}
}
