package org.apache.coyote.http11.response;

public class ResponseBody {

	private final String responseBody;
	private final ContentType contentType;

	public ResponseBody(String responseBody, ContentType contentType) {

		this.responseBody = responseBody;
		this.contentType = contentType;
	}

	public byte[] createResponseFormatToBytes() {
		return String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
			"Content-Length: " + responseBody.getBytes().length + " ",
			"",
			responseBody).getBytes();
	}
}
