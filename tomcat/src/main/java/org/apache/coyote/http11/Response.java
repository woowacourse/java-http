package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class Response {

	public String createResponse(String url, String ContentType) throws IOException {
		final String path = getResourcesPath(url);
		final URL resource = getClass().getClassLoader().getResource(path);
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		String response = createResponseFormat("Content-Type: " + ContentType + ";charset=utf-8 ", responseBody);
		return response;
	}

	private String getResourcesPath(String url) {
		return "static" + url;
	}

	public String createResponseFormat(String ContentType, String responseBody) {
		return String.join("\r\n",
			"HTTP/1.1 200 OK ",
			ContentType,
			"Content-Length: " + responseBody.getBytes().length + " ",
			"",
			responseBody);
	}
}
