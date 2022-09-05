package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class LoginHttpResponse implements HttpResponse {

	private final ResponseBody responseBody;

	public LoginHttpResponse(String path, ContentType contentType) throws IOException {
		this.responseBody = createResponseBody(path, contentType);
	}

	protected String getResourcesPath(String url) {
		return "static" + url;
	}

	private ResponseBody createResponseBody(String path, ContentType contentType) throws IOException {
		URL resource = getClass().getClassLoader().getResource(getResourcesPath(path));
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		return new ResponseBody(responseBody, contentType);
	}

	public byte[] createResponseFormatToBytes() {
		return responseBody.createResponseFormatToBytes();
	}
}
