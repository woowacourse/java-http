package org.apache.coyote.http11;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceReader {

    private final static String BASE_PATH = "static";

    private final ClassLoader classLoader = getClass().getClassLoader();

    public String read(String path) throws IOException {
        InputStream resourceAsStream = classLoader.getResourceAsStream(BASE_PATH + path);
        if (resourceAsStream == null) {
            return null;
        }

        return InputStreamConvertor.convertToString(resourceAsStream, StandardCharsets.UTF_8);
    }
}
