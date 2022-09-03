package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.response.HttpResponse2;

public class FileSearcher {

	private static final String STATIC_PATH = "static";

	private FileSearcher() {
	}

	public static String loadContent(final String fileName) {
		final URL path = HttpResponse2.class.getClassLoader().getResource(STATIC_PATH + fileName);

		final String content;
		try {
			content = new String(Files.readAllBytes(new File(path.getFile()).toPath()));
		} catch (IOException e) {
			throw new IllegalArgumentException(String.format("잘못된 경로입니다. [%s]", fileName));
		}
		return content;
	}
}
