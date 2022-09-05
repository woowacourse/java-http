package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class CommonHttpResponse implements HttpResponse {

	private final ResponseBody responseBody;

	public CommonHttpResponse(String url, ContentType contentType) throws IOException {
		this.responseBody = createResponseBody(url, contentType);
	}

	protected String getResourcesPath(String url) {
		return "static" + url;
	}

	private ResponseBody createResponseBody(String url, ContentType contentType) throws IOException {
		String path = getResourcesPath(url);
		URL resource = getClass().getClassLoader().getResource(getResourcesPath(url));
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		return new ResponseBody(responseBody, contentType);
	}

	public byte[] createResponseFormatToBytes() {
		return responseBody.createResponseFormatToBytes();
	}
}
