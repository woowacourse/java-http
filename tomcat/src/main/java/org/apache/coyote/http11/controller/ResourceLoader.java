package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceLoader {

    public static byte[] loadResource(String resourceName) throws URISyntaxException, IOException {
        URL url = ResourceLoader.class.getClassLoader().getResource(resourceName);
        if (url == null) {
            throw new IllegalArgumentException("존재하지 않는 리소스 입니다." + resourceName);
        }

        Path path = Path.of(url.toURI());
        return Files.readAllBytes(path);
    }
}
