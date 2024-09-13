package org.apache.coyote.http11.http.response;

import java.nio.charset.StandardCharsets;

public class HttpResponseBody {

	private String contentType;
	private byte[] content;

	public HttpResponseBody() {
	}

	public boolean isExist() {
		return contentType != null && content != null;
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

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String toResponseMessage() {
		return new String(content, StandardCharsets.UTF_8);
	}
}
