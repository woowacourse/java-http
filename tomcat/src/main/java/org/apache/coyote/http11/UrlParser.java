package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class UrlParser {

	private final String resource;
	private final Map<String, String> queries = new HashMap<>();

	public UrlParser(final String uri) {
		if (!uri.contains("?")) {
			resource = uri;
		} else {
			final String[] uriElement = uri.split("\\?");
			resource = uriElement[0] + ".html";
			final String[] queryElements = uriElement[1].split("&");
			for (String element : queryElements) {
				final String[] queryValueElement = element.split("=");
				queries.put(queryValueElement[0], queryValueElement[1]);
			}
		}
	}

	public String getResource() {
		return resource;
	}

	public Map<String, String> getQueries() {
		return Map.copyOf(queries);
	}
}
