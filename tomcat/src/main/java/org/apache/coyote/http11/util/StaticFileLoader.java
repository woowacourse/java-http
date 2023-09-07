package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileLoader {

    private static final String STATIC = "static";

    private StaticFileLoader() {
    }

    public static String load(String fileName) throws IOException {
        URL resource = StaticFileLoader.class.getClassLoader().getResource(STATIC + fileName);
        if (resource == null) {
            return "";
        }
        Path path = new File(resource.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
