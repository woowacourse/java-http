package org.apache.coyote.file;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceReader {

    private static final String STATIC_PATH = "static/";

    private ResourceReader() {
    }

    public static byte[] read(String file) throws URISyntaxException, IOException {
        String resourcePath = STATIC_PATH + file;
        Path filePath = Paths.get(ResourceReader.class.getClassLoader().getResource(resourcePath).toURI());
        return Files.readAllBytes(filePath);
    }
}
