package org.apache.coyote.http11.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    private static final String STATIC_PATH = "static";

    private FileReader() {
    }

    public static String read(final String resource) {
        try {
            final Path path = Paths.get(ClassLoader
                    .getSystemResource(STATIC_PATH + resource)
                    .getFile()
            );

            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            return "";
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
    }
}
