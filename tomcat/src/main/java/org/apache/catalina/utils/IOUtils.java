package org.apache.catalina.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class IOUtils {

    private static final String STATIC_PATH = "static";

    public static String readData(final BufferedReader bufferedReader,
                                  final int length) throws IOException {
        final char[] bodyArr = new char[length];
        bufferedReader.read(bodyArr, 0, length);
        return String.copyValueOf(bodyArr);
    }

    public static String readResourceFile(final String fileName) throws IOException, URISyntaxException {
        final ClassLoader classLoader = IOUtils.class.getClassLoader();
        final URI resource = Objects.requireNonNull(classLoader.getResource(STATIC_PATH + fileName)).toURI();
        final Path path = Path.of(resource.getPath());

        return Files.readString(path);
    }
}
