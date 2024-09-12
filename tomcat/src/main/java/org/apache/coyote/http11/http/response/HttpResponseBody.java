package org.apache.coyote.http11.http.response;

import java.nio.charset.StandardCharsets;

public class HttpResponseBody {

	private final String contentType;
	private final byte[] content;

	public HttpResponseBody(String contentType, byte[] content) {
		this.contentType = contentType;
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getContent() {
		return content;
	}

	public int getContentLength() {
		return content.length;
	}

	public String toResponseMessage() {
		return new String(content, StandardCharsets.UTF_8);
	}
}
