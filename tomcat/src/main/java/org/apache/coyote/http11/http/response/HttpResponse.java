package org.apache.coyote.http11.http.response;

public class HttpResponse {

	private static final String HTTP_VERSION = "HTTP/1.1";

	private final HttpResponseStartLine startLine;
	private final HttpResponseHeader header;
	private final HttpResponseBody body;

	public static HttpResponse ok(HttpResponseHeader header, HttpResponseBody body) {
		return new HttpResponse(new HttpResponseStartLine(HTTP_VERSION, HttpStatusCode.OK), header, body);
	}

	public static HttpResponse notFound(HttpResponseHeader header, HttpResponseBody body) {
		return new HttpResponse(new HttpResponseStartLine(HTTP_VERSION, HttpStatusCode.NOT_FOUND), header, body);
	}

	public HttpResponse(HttpResponseStartLine startLine, HttpResponseHeader header, HttpResponseBody body) {
		this.startLine = startLine;
		this.header = header;
		this.body = body;
	}

	public String toResponseMessage() {
		String contentCharset = ";charset=utf-8";
		header.addHeader("Content-Type", body.getContentType() + contentCharset);
		header.addHeader("Content-Length", String.valueOf(body.getContentLength()));

		return String.join("\r\n"
			, startLine.toResponseMessage()
			, header.toResponseMessage()
			, ""
			, body.toResponseMessage()
		);
	}
}
