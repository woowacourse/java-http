package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.exception.ResourceNotFoundException;

public class FileReader {

    private static final String FILE_EXTENSION_PREFIX = ".";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String HTML_FILE_EXTENSION = ".html";

    private static final FileReader INSTANCE = new FileReader();

    public static FileReader getInstance() {
        return INSTANCE;
    }

    public String generate(String resource) throws IOException {
        String resourceName = STATIC_RESOURCE_PATH + resource;
        URL url = getClass().getClassLoader()
                .getResource(resourceName);
        checkNotFoundResource(url);
        Path path = new File(url.getFile()).toPath();
        return Files.readString(path);
    }

    private void checkNotFoundResource(URL url) {
        if (url == null) {
            throw new ResourceNotFoundException();
        }
    }

    public boolean isValidFile(String resource) {
        String resourceName = STATIC_RESOURCE_PATH + resource;
        URL url = getClass().getClassLoader()
                .getResource(resourceName);
        return url != null;
    }
}
