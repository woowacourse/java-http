package org.apache.coyote.util;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private static final String STATIC_PATH_NAME = "/static";

    private FileReader() {
    }

    public static String readStaticFile(String resourcePath, Class<?> classes) {
        try {
            URL resourceURL = classes.getResource(STATIC_PATH_NAME + resourcePath);
            return Files.readString(Paths.get(resourceURL.toURI()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }
    }
}
