package org.apache.coyote.file;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticFileHandler {

    public static String getFileLines(final String path) throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
