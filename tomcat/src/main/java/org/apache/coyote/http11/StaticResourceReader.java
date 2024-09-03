package org.apache.coyote.http11;


import java.io.IOException;
import java.io.InputStream;

public class StaticResourceReader {

    private final static String BASE_PATH = "static";

    private final ClassLoader classLoader = getClass().getClassLoader();

    public byte[] read(String path) throws IOException {
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(BASE_PATH + path)) {
            if (resourceAsStream == null) {
                return null;
            }

            return resourceAsStream.readAllBytes();
        }
    }
}
