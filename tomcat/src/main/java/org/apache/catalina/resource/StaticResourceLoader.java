package org.apache.catalina.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class StaticResourceLoader {

    private static final String STATIC_DIRECTORY = "static";

    public byte[] load(String path) throws IOException {
        String resourcePath =STATIC_DIRECTORY + path;

        try (InputStream inputStream =
                     getClass().getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new FileNotFoundException(resourcePath);
            }

            return inputStream.readAllBytes();
        }
    }
}
