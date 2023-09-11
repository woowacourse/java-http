package org.apache.coyote.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.exception.ResourceNotFoundException;

public class ResourceResolver {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private static final String STATIC_PATH = "static";

    private ResourceResolver() {
    }

    public static String resolve(String path) {
        URL resource = classLoader.getResource(STATIC_PATH + path);
        if (resource == null) {
            throw new ResourceNotFoundException("해당 리소스를 찾을 수 없습니다.");
        }
        File file = new File(resource.getFile());
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
