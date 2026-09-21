package org.apache.catalina.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceLoader {

    public String load(String path) throws IOException {
        String resourceName = "static" + path;

        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (resource == null) {
                throw new FileNotFoundException(resourceName);
            }
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
