package org.apache.coyote.http11.response;

public class HttpResponse {

	private static final String CRLF = " \r\n";

	private final HttpStatusLine httpStatusLine;
	private final HttpHeaders headers;
	private final String responseBody;

	private HttpResponse(HttpStatusLine httpStatusLine, HttpHeaders headers, String responseBody) {
		this.httpStatusLine = httpStatusLine;
		this.headers = headers;
		this.responseBody = responseBody;
	}

	public static HttpResponse of(HttpStatusLine statusLine, HttpHeaders headers, String responseBody) {
		return new HttpResponse(statusLine, headers, responseBody);
	}

	public String generateHttpResponse() {
		return String.join(CRLF, httpStatusLine.generateStatusLine(),
			headers.generateHeadersResponse(),
			responseBody);
	}
}
