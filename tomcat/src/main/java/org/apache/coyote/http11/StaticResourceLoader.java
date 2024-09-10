package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticResourceLoader {

    private static final ClassLoader classLoader = Http11Processor.class.getClassLoader();
    private static final String STATIC_RESOURCE_PATH = "static/";

    private StaticResourceLoader() {
    }

    // TODO: 파일이 메모리보다 큰 경우, Stream을 활용해야 한다
    public static byte[] load(String uri) {
        try {
            URL resource = classLoader.getResource(STATIC_RESOURCE_PATH + uri);
            if (resource == null) {
                throw new IllegalArgumentException("Resource not found: " + uri);
            }
            Path path = Paths.get(resource.getFile());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load resource: " + uri);
        }
    }
}
