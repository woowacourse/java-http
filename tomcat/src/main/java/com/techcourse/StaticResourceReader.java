package com.techcourse;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class StaticResourceReader {

    private final static String BASE_PATH = "static";

    private final ClassLoader classLoader = getClass().getClassLoader();

    public byte[] read(String path) throws IOException {
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(Paths.get(BASE_PATH, path).toString())) {
            if (resourceAsStream == null) {
                return null;
            }

            return resourceAsStream.readAllBytes();
        }
    }
}
