package org.apache.coyote.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {

    private static final String STATIC_DIRECTORY = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";

    public static String readFile(String path) throws IOException {
        Path filePath = null;
        try {
            URL resource = FileUtils.class
                    .getClassLoader()
                    .getResource(STATIC_DIRECTORY + path);

            filePath = Path.of(resource.getPath());
        } catch (NullPointerException e) {
            URL resource = Objects.requireNonNull(FileUtils.class
                    .getClassLoader()
                    .getResource(STATIC_DIRECTORY + NOT_FOUND_PAGE));

            filePath = Path.of(resource.getPath());
        }

        return new String(Files.readAllBytes(filePath));
    }
}
