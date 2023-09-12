package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestUri {

	private final String path;
	private final Map<String, String> queryParams = new HashMap<>();

	public RequestUri(final String path) {
		this(path, new HashMap<>());
	}

	public RequestUri(final String path, final Map<String, String> queryParams) {
		this.path = path;
		this.queryParams.putAll(queryParams);
	}

	public boolean hasPath(final String path) {
		return Objects.equals(this.path, path);
	}

	public String findQueryParam(final String key) {
		return queryParams.get(key);
	}

	public String getPath() {
		return path;
	}
}
