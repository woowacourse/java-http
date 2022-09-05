package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.response.ContentType;

public class Response {

	public String createResponse(String url, ContentType contentType) throws IOException {
		final String path = getResourcesPath(url);
		final URL resource = getClass().getClassLoader().getResource(path);
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		String response = createResponseFormat(contentType, responseBody);
		return response;
	}

	private String getResourcesPath(String url) {
		return "static" + url;
	}

	public String createResponseFormat(ContentType ContentType, String responseBody) {
		return String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"ContentType: " + ContentType + ";charset=utf-8 ",
			"Content-Length: " + responseBody.getBytes().length + " ",
			"",
			responseBody);
	}
}
