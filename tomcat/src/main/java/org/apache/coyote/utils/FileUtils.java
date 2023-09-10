package org.apache.coyote.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    private static final String STATIC_DIRECTORY = "static";

    public static String readFile(String path) throws IOException {
        URL resource = FileUtils.class
                .getClassLoader()
                .getResource(STATIC_DIRECTORY + path);

        Path filePath = Path.of(resource.getPath());

        return new String(Files.readAllBytes(filePath));
    }
}
