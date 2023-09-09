package org.apache.coyote.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {

    private static final String STATIC_DIRECTORY = "static";

    public static String readFile(String path) throws IOException {
        final Path filePath = Path.of(Objects.requireNonNull(FileUtils.class.getClassLoader().getResource(STATIC_DIRECTORY + path)).getPath());

        return new String(Files.readAllBytes(filePath));
    }
}
