package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.exception.StaticResourceReadException;

public class StaticResourceResolver {

	private StaticResourceResolver() {

	}

	public static String resolve(final URL url) {
		try {
			return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
		} catch (final IOException e) {
			throw new StaticResourceReadException(e);
		}
	}
}
