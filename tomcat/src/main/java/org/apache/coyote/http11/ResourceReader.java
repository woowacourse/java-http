package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class ResourceReader {

    public static String read(String resourcePath) {
        Path path = resolvePath(resourcePath);
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            return bufferedReader.lines()
                    .collect(Collectors.joining("\n", "", "\n"));
        } catch (IOException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }
    }

    private static Path resolvePath(String resourcePath) {
        URL resource = ResourceReader.class.getResource("/static" + resourcePath);
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException | NullPointerException exception) {
            throw new IllegalArgumentException(exception);
        }
    }
}
