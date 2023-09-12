package org.apache.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileReader {

    private static final String DEFAULT_RESOURCE_LOCATION = "static";

    private FileReader() {
    }

    public static String read(String path) throws IOException {
        final URL url = FileReader.class.getClassLoader().getResource(DEFAULT_RESOURCE_LOCATION + path);
        if (url == null) {
            URL notFoundUrl = FileReader.class.getClassLoader().getResource(DEFAULT_RESOURCE_LOCATION + "/404.html");
            return new String(Files.readAllBytes(new File(notFoundUrl.getFile()).toPath()));
        }
        return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }

    public static URL parseURL(String path) {
        return FileReader.class.getClassLoader().getResource(DEFAULT_RESOURCE_LOCATION + path);
    }
}
