package org.apache.coyote.http11.http.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.http.HttpHeader;

public class HttpResponse {

	private final HttpResponseStartLine startLine;
	private final HttpResponseHeader header;
	private final HttpResponseBody body;

	public HttpResponse() {
		this(new HttpResponseStartLine(), new HttpResponseHeader(), new HttpResponseBody());
	}

	private HttpResponse(HttpResponseStartLine startLine, HttpResponseHeader header, HttpResponseBody body) {
		this.startLine = startLine;
		this.header = header;
		this.body = body;
	}

	public String toResponseMessage() {
		if (body.isExist()) {
			return createMessageWithBody();
		}
		return createMessageWithoutBody();
	}

	private String createMessageWithBody() {
		String contentCharset = ";charset=utf-8";
		addHeader(HttpHeader.CONTENT_TYPE.getName(), body.getContentType() + contentCharset);
		addHeader(HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(body.getContent().length));

		List<String> lines = createBaseMessage();
		lines.add(body.toResponseMessage());

		return String.join("\r\n", lines);
	}

	private String createMessageWithoutBody() {
		return String.join("\r\n", createBaseMessage());
	}

	public List<String> createBaseMessage() {
		List<String> lines = new ArrayList<>();
		lines.add(startLine.toResponseMessage());
		lines.add(header.toResponseMessage());
		lines.add("");
		return lines;
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.startLine.setStatusCode(statusCode);
	}

	public void addHeader(String key, String value) {
		this.header.addHeader(key, value);
	}

	public void addHeader(String key, List<String> values) {
		this.header.addHeader(key, values);
	}

	public void setBody(String contentType, byte[] content) {
		this.body.setContent(content);
		this.body.setContentType(contentType);
	}
}
