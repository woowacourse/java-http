package org.apache.coyote.http11.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileReader {

    public static String readByName(final String name) throws IOException {
        if (name.equals("/")) {
            return "Hello world!";
        }
        if (name.startsWith("/")) {
            return readContent("static" + name);
        }
        return readContent("static/" + name);
    }

    private static String readContent(final String target) throws FileNotFoundException, IOException {
        try (final var stream = FileReader.class.getClassLoader().getResourceAsStream(target)) {
            if (stream == null) {
                throw new FileNotFoundException();
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
