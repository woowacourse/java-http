package com.techcourse.web;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

	private final String protocol;
	private final HttpStatusCode statusCode;
	private final Map<String, String> headers;
	private final HttpResponse.Body body;

	private HttpResponse(String protocol, HttpStatusCode statusCode, Map<String, String> headers,
		HttpResponse.Body body) {
		this.protocol = protocol;
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = body;
	}

	public static HttpResponse.Builder builder() {
		return new HttpResponse.Builder();
	}

	public String createResponseMessage() {
		StringBuilder response = new StringBuilder();

		// start line
		response.append(protocol)
			.append(" ")
			.append(statusCode.getCode())
			.append(" ")
			.append(statusCode.getMessage())
			.append(" ")
			.append("\r\n");

		// header
		headers.forEach((key, value) -> response.append(key).append(": ").append(value).append(" ").append("\r\n"));

		// crlf
		response.append("\r\n");

		// body
		if (body != null) {
			response.append(body.getContent());
		}

		return response.toString();
	}

	public String getProtocol() {
		return protocol;
	}

	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Body getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "HttpResponse{" +
			"protocol='" + protocol + '\'' +
			", statusCode=" + statusCode +
			", headers=" + headers +
			", body=" + body +
			'}';
	}

	public static class Body {

		private static final String DEFAULT_CONTENT_CHARSET = ";charset=utf-8";

		private final String contentType;
		private final int contentLength;
		private final String content;

		public static HttpResponse.Body fromPath(String path) throws IOException {
			String fileName = path.contains(".") ? path : path + ".html";
			URL resource = HttpResponse.Body.class.getResource("/static" + fileName);
			if (resource == null) {
				throw new IllegalArgumentException("resource not found: " + "/static" + fileName);
			}

			Path resourcePath = Paths.get(resource.getPath());

			String contentType = Files.probeContentType(resourcePath) + DEFAULT_CONTENT_CHARSET;
			String body = Files.readString(resourcePath);
			int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

			return new HttpResponse.Body(contentType, contentLength, body);
		}

		public static HttpResponse.Body fromString(String body) {
			String contentType = "text/html" + DEFAULT_CONTENT_CHARSET;
			int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

			return new HttpResponse.Body(contentType, contentLength, body);
		}

		private Body(String contentType, int contentLength, String content) {
			this.contentType = contentType;
			this.contentLength = contentLength;
			this.content = content;
		}

		public String getContentType() {
			return contentType;
		}

		public int getContentLength() {
			return contentLength;
		}

		public String getContent() {
			return content;
		}

		@Override
		public String toString() {
			return "Body{" +
				"contentType='" + contentType + '\'' +
				", contentLength=" + contentLength +
				", content='" + content + '\'' +
				'}';
		}
	}

	public static class Builder {

		private String protocol;
		private HttpStatusCode statusCode;
		private Map<String, String> headers;
		private HttpResponse.Body body;

		public Builder() {
			headers = new LinkedHashMap<>();
		}

		public Builder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		public Builder statusCode(HttpStatusCode statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public Builder header(String key, String value) {
			headers.put(key, value);
			return this;
		}

		public Builder body(HttpResponse.Body body) {
			this.body = body;
			headers.put("Content-Type", body.getContentType());
			headers.put("Content-Length", String.valueOf(body.getContentLength()));
			return this;
		}

		public HttpResponse build() {
			return new HttpResponse(protocol, statusCode, headers, body);
		}
	}
}
