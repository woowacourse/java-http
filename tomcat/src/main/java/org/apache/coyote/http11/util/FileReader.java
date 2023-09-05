package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileReader {

    public static String read(final String resourcePath) throws IOException {
        final URL resource = FileReader.class.getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            return "";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
