package org.apache.coyote.http11;

public class HttpResponse {

	private final StatusCode statusCode;
	private final String responseBody;
	private final ContentType contentType;

	public HttpResponse(StatusCode statusCode, String responseBody, ContentType contentType) {
		this.statusCode = statusCode;
		this.responseBody = responseBody;
		this.contentType = contentType;
	}

	public byte[] getBytes() {
		return String.join("\r\n",
				"HTTP/1.1 " + statusCode.getCode() + statusCode.getMessage(),
				"Content-Type: " + contentType.getType() + ";charset=utf-8 ",
				"Content-Length: " + responseBody.getBytes().length + " ",
				"",
				responseBody)
			.getBytes();
	}
}
