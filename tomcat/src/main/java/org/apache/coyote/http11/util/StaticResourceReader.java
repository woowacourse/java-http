package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.InputStream;

public class StaticResourceReader {

    public static String read(String resourceUri) throws IOException {
        try (InputStream inputStream = StaticResourceReader.class
                .getClassLoader()
                .getResourceAsStream("static" + resourceUri)) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
