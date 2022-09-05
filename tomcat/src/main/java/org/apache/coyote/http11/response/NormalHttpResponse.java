package org.apache.coyote.http11.response;

import java.io.IOException;

public class NormalHttpResponse implements HttpResponse {

	private final ResponseBody responseBody;

	public NormalHttpResponse(String responseBody, ContentType contentType) throws IOException {
		this.responseBody = new ResponseBody(responseBody, contentType);
	}

	public byte[] createResponseFormatToBytes() {
		return responseBody.createResponseFormatToBytes();
	}
}
