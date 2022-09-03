package org.apache.coyote.http11.support;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileReader {

    private static final String STATIC_PATH_NAME = "static";

    public String readStaticFile(String resourcePath) {
        URL resourceURL = getClass().getClassLoader().getResource(STATIC_PATH_NAME + resourcePath);
        try {
            File file = new File(Objects.requireNonNull(resourceURL).toURI());
            Path path = file.getAbsoluteFile().toPath();
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
