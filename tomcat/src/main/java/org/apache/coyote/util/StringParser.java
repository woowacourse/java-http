package org.apache.coyote.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http.HttpRequest;

public class StringParser {

    private static final String ELEMENT_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String FILE_EXTENSION = ".";
    private static final String HOME = "/";
    private static final String TEXT_HTML = "text/html";

    private StringParser() {
    }

    public static Map<String, String> toMap(final String source) {
        return Arrays.stream(source.split(ELEMENT_DELIMITER))
                .map(it -> it.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(it -> it[0], it -> it[1], (a, b) -> b));
    }

    public static String toMimeType(final String httpRequestUri) throws IOException {
        if (httpRequestUri.equals(HOME) || !httpRequestUri.contains(FILE_EXTENSION)) {
            return TEXT_HTML;
        }
        final Path resourcePath = getResourcePath(httpRequestUri);

        return Files.probeContentType(resourcePath);
    }

    private static Path getResourcePath(final String httpRequestUri) {
        final String resourcePath = HttpRequest.class.getClassLoader()
                .getResource("static" + httpRequestUri)
                .getPath();
        return Path.of(resourcePath);
    }
}
