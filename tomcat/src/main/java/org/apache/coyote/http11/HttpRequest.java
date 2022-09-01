package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpRequest {

	private final String value;

	public HttpRequest(InputStream inputStream) throws IOException {
		BufferedReader requestReader = new BufferedReader(new InputStreamReader(inputStream));
		List<String> requestLines = new ArrayList<>();
		while (requestReader.ready()) {
			requestLines.add(requestReader.readLine());
		}
		this.value = String.join("\r\n", requestLines);
	}

	public String getUrl() {
		return value
			.split("\r\n")[0]
			.split(" ")[1];
	}

	public boolean isStaticResourceRequest() {
		return getUrl().contains(".");
	}

	public ContentType getContentType() {
		return ContentType.from(getUrl().split("\\.")[1]);
	}

	public String getQueryString(String key) {
		return Arrays.stream(
				getUrl().split("\\?")[1]
					.split("&")
			)
			.collect(toMap(
				value -> value.split("=")[0],
				value -> value.split("=")[1])
			)
			.get(key);
	}
}
