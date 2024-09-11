package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.http11.response.ResponseBody;

public class ResourceLoader {

	private static final ResourceLoader instance = new ResourceLoader();
	private static final String RESOURCE_PATH = "/static";
	private static final String ROOT_PATH = "/";
	private static final String ROOT_PATH_CONTENT = "Hello world!";
	private static final String ROOT_PATH_CONTENT_TYPE = "text/html";

	private ResourceLoader() {
	}

	public static ResourceLoader getInstance() {
		return instance;
	}

	public ResponseBody loadResource(String requestUrl) throws IOException {
		if (requestUrl.equals(ROOT_PATH)) {
			return ResponseBody.builder()
				.content(ROOT_PATH_CONTENT.getBytes())
				.contentType(ROOT_PATH_CONTENT_TYPE)
				.build();
		}

		return readResource(requestUrl);
	}

	private ResponseBody readResource(String requestUrl) throws IOException {
		String fileName = RESOURCE_PATH + requestUrl;
		Path filePath = getPath(fileName);

		return ResponseBody.builder()
			.contentType(Files.probeContentType(filePath))
			.content(Files.readAllBytes(filePath))
			.build();
	}

	private Path getPath(String fileName) {
		URL resource = getClass().getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("Resource not found. resource: " + fileName);
		}

		return Path.of(resource.getPath());
	}
}
