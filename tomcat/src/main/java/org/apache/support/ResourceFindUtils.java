package org.apache.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.exception.FileIOException;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFindUtils {
    private static final Logger log = LoggerFactory.getLogger(ResourceFindUtils.class);

    public static String getResourceFile(String resource) {
        final Path path = extractPath(resource);

        try {
            final String responseBody = Files.readString(path);
            return responseBody;
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new FileIOException();
        }
    }

    private static Path extractPath(String resource) {
        try {
            URL url = ResourceFindUtils.class.getClassLoader().getResource("static" + resource);
            final File file = new File(url.getPath());
            final Path path = file.toPath();
            return path;
        } catch (NullPointerException exception) {
            throw new ResourceNotFoundException();
        }
    }
}
