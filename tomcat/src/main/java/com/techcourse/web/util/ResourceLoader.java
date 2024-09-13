package com.techcourse.web.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import com.techcourse.web.Resource;

public class ResourceLoader {

	private static final ResourceLoader instance = new ResourceLoader();
	private static final String RESOURCE_PATH = "/static";

	private ResourceLoader() {
	}

	public static ResourceLoader getInstance() {
		return instance;
	}

	public Resource loadResource(String filePath) throws IOException {
		Path path = getPath(RESOURCE_PATH + filePath);

		return new Resource(Files.probeContentType(path), Files.readAllBytes(path));
	}

	private Path getPath(String fileName) {
		URL resource = getClass().getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("Resource not found. resource: " + fileName);
		}

		return Path.of(resource.getPath());
	}
}
