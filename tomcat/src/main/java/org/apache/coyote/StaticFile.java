package org.apache.coyote;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class StaticFile {

    public static String load(final String path) throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath()));
    }

    public static ContentType parseContentType(final String path) {
        final String extension = path.substring(path.lastIndexOf(".") + 1);
        return ContentType.from(extension);
    }
}
