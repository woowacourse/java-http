package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

	private final HttpMethod method;
	private final String url;
	private final Map<String, String> headers;
	private final String body;

	public HttpRequest(BufferedReader requestReader) throws IOException {
		String[] startLine = requestReader.readLine().split(" ");
		this.method = HttpMethod.from(startLine[0]);
		this.url = startLine[1];
		this.headers = extractHeaders(requestReader);
		this.body = extractBody(requestReader);
	}

	private Map<String, String> extractHeaders(BufferedReader requestReader) throws IOException {
		Map<String, String> headers = new HashMap<>();
		String line;
		while (!(line = requestReader.readLine()).isBlank()) {
			String[] header = line.split(": ");
			headers.put(header[0], header[1]);
		}
		return headers;
	}

	private String extractBody(BufferedReader requestReader) throws IOException {
		String length = headers.getOrDefault(Header.CONTENT_LENGTH.getName(), null);
		if (length == null) {
			return null;
		}
		int contentLength = Integer.parseInt(length);
		char[] body = new char[contentLength];
		requestReader.read(body, 0, contentLength);
		return new String(body);
	}

	public String getUrl() {
		return url;
	}

	public boolean isStaticResourceRequest() {
		return url.contains(".");
	}

	public ContentType getContentType() {
		return ContentType.from(extractExtension());
	}

	private String extractExtension() {
		return url.split("\\.")[1];
	}

	public String getQueryString(String key) {
		String contentType = headers.get(Header.CONTENT_TYPE.getName());
		List<String> requestParams = extractRequestParams(contentType);
		return requestParams.stream()
			.filter(param -> param.split("=")[0].equals(key))
			.map(param -> param.split("=")[1])
			.findAny()
			.orElse("");
	}

	private List<String> extractRequestParams(String contentType) {
		List<String> requestParams = new ArrayList<>();
		if (ContentType.FORM_DATA.equals(contentType)) {
			String[] params = body.split("&");
			requestParams.addAll(List.of(params));
		}
		if (url.contains("?")) {
			String[] params = url.split("\\?")[1]
				.split("&");
			requestParams.addAll(List.of(params));
		}
		return requestParams;
	}

	public String getMethod() {
		return method.name();
	}

	@Override
	public String toString() {
		return "===HttpRequest===" + "\r\n" +
			"method='" + method + "\r\n" +
			"url='" + url + "\r\n" +
			"headers=" + headers + "\r\n" +
			"body='" + body + "\r\n" +
			'}';
	}
}
