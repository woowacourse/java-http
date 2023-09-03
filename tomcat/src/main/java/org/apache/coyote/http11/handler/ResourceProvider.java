package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResourceProvider {

	private static final String RESOURCE_ROOT_PATH = "static";

	public static String provide(final String path) {
		final var resource = ResourceProvider.class.getClassLoader()
			.getResource(RESOURCE_ROOT_PATH + path);

		if (resource == null) {
			throw new IllegalArgumentException("해당하는 자원을 찾을 수 없습니다.");
		}

		final var file = new File(resource.getPath()).toPath();

		if (Files.isRegularFile(file)) {
			try {
				return new String(Files.readAllBytes(file));
			} catch (IOException e) {
				throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
			}
		}

		throw new IllegalArgumentException("올바르지 않은 파일입니다.");
	}
}
