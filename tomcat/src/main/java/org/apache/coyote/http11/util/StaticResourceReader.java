package org.apache.coyote.http11.util;

import java.io.IOException;

public class StaticResourceReader {

    public static String read(String resourceUri) throws IOException {
        return new String(
                StaticResourceReader.class
                        .getClassLoader()
                        .getResourceAsStream("static" + resourceUri)
                        .readAllBytes()
        );
    }
}
