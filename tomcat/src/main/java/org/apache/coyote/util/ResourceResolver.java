package org.apache.coyote.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class ResourceResolver {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private static final String STATIC_PATH = "static";

    private ResourceResolver() {
    }

    public static String resolve(String path) throws IOException {
        URL resource = classLoader.getResource(STATIC_PATH + path);
        if (resource == null) {
            throw new NoSuchFileException("해당 리소스를 찾을 수 없습니다.");
        }
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
