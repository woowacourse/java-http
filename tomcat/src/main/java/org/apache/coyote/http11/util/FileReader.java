package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.NotFoundException;

public class FileReader {

    public static String read(final String resourcePath) {
        try {
            URL resource = FileReader.class
                    .getClassLoader()
                    .getResource("static" + resourcePath);
            String filePath = Objects.requireNonNull(resource)
                    .getFile();
            Path path = new File(filePath).toPath();
            return new String(Files.readAllBytes(path));
        } catch (IOException | NullPointerException e) {
            throw new NotFoundException("File not found.");
        }
    }
}
