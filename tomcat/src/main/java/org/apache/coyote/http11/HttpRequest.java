package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

	private final String method;
	private final HttpRequestTarget requestTarget;
	private final String protocol;
	private final Map<String, String> headers;
	private final String body;

	public HttpRequest(List<String> httpRequest) {
		String[] requestLine = httpRequest.getFirst().split(" ");
		this.method = requestLine[0];
		this.requestTarget = new HttpRequestTarget(requestLine[1]);
		this.protocol = requestLine[2];
		this.headers = createHeaders(httpRequest);
		this.body = createBody(httpRequest);
	}

	private Map<String, String> createHeaders(List<String> httpRequest) {
		Map<String, String> headers = new HashMap<>();
		httpRequest.stream()
			.skip(1)
			.takeWhile(header -> !header.isBlank())
			.forEach(header -> {
				String[] keyValue = header.split(":", 2);
				headers.put(keyValue[0], keyValue[1].strip());
			});
		return headers;
	}

	private String createBody(List<String> httpRequest) {
		return httpRequest.stream()
			.skip(1)
			.dropWhile(header -> !header.isBlank())
			.collect(Collectors.joining("\r\n"));
	}

	public String getMethod() {
		return method;
	}

	public String getRequestPath() {
		return requestTarget.path;
	}

	public Map<String, String> getRequestQuery() {
		return requestTarget.query;
	}

	public String getProtocol() {
		return protocol;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "HttpRequest{" +
			"method='" + method + '\'' +
			", requestTarget=" + requestTarget +
			", protocol='" + protocol + '\'' +
			", headers=" + headers +
			", body='" + body + '\'' +
			'}';
	}

	static class HttpRequestTarget {

		private final String path;
		private final Map<String, String> query;

		public HttpRequestTarget(String requestTarget) {
			String[] parts = requestTarget.split("\\?");
			String query = parts.length == 2 ? parts[1] : "";
			this.path = parts[0];
			this.query = createQuery(query);
		}

		private Map<String, String> createQuery(String query) {
			Map<String, String> queries = new HashMap<>();

			if (query.isBlank()) {
				return queries;
			}

			String[] parts = query.split("&");
			for (String part : parts) {
				String[] keyValue = part.split("=");
				queries.put(keyValue[0], keyValue[1]);
			}
			return queries;
		}

		@Override
		public String toString() {
			return "HttpRequestTarget{" +
				"path='" + path + '\'' +
				", query=" + query +
				'}';
		}
	}
}
