package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UrlGenerator {

	private UrlGenerator() {
	}

	public static String getUrl(InputStream inputStream) throws IOException {
		String line = new BufferedReader(new InputStreamReader(inputStream))
			.readLine();

		if (line == null) {
			return null;
		}

		return findUrl(line);
	}

	private static String findUrl(String line) {
		return line.split(" ")[1];
	}
}
