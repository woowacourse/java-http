package nextstep.jwp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class IOUtils {

	private static final String STATIC_PATH = "static";

	public static String readData(final BufferedReader bufferedReader, final int length) throws IOException {
		final char[] bodyArr = new char[length];
		bufferedReader.read(bodyArr, 0, length);
		return String.copyValueOf(bodyArr);
	}

	public static String readResourceFile(final String fileName) throws IOException, URISyntaxException {
		final URL url = Objects.requireNonNull(
			Thread.currentThread().getContextClassLoader().getResource(STATIC_PATH + fileName));
		final File file = new File(url.getFile());
		return new String(Files.readAllBytes(file.toPath()));
	}
}
