package org.apache.coyote.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileContentParser {

    private FileContentParser() {
    }

    public static String parseContent(final Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseContentType(final Path path) {
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
