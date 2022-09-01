package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class StaticResourceUtil {

	private static final String STATIC_RESOURCE_ROOT_PATH = "static/";

	private StaticResourceUtil() {
	}

	public static String getContent(String filePath) throws IOException {
		final URL resource = StaticResourceUtil.class
			.getClassLoader()
			.getResource(STATIC_RESOURCE_ROOT_PATH + filePath);
		return new String(Files.readAllBytes(getPath(resource)));
	}

	private static Path getPath(URL resource) {
		return new File(Objects.requireNonNull(resource).getFile()).toPath();
	}
}
