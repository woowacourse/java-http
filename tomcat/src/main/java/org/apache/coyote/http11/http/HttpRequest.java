package org.apache.coyote.http11.http;

import static java.util.stream.Collectors.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

	private final String method;
	private final String url;
	private final Map<String, String> headers;
	private final String body;

	public HttpRequest(InputStream inputStream) throws IOException {
		BufferedReader requestReader = new BufferedReader(new InputStreamReader(inputStream));

		String[] startLine = requestReader.readLine().split(" ");
		this.method = startLine[0];
		this.url = startLine[1];
		this.headers = extractHeaders(requestReader);
		this.body = extractBody(requestReader);
	}

	private Map<String, String> extractHeaders(BufferedReader requestReader) throws IOException {
		Map<String, String> headers = new HashMap<>();
		while (requestReader.ready()) {
			String line = requestReader.readLine();
			if (line.isBlank()) {
				return headers;
			}
			String[] header = line.split(": ");
			headers.put(header[0], header[1]);
		}
		return headers;
	}

	private String extractBody(BufferedReader requestReader) throws IOException {
		List<String> bodyLines = new ArrayList<>();
		while (requestReader.ready()) {
			bodyLines.add(requestReader.readLine());
		}
		return String.join("\r\n", bodyLines);
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
		return Arrays.stream(
				url.split("\\?")[1]
					.split("&")
			)
			.collect(toMap(
				value -> value.split("=")[0],
				value -> value.split("=")[1])
			)
			.get(key);
	}
}
