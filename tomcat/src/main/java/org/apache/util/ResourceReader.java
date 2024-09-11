package org.apache.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceReader {

    private ResourceReader() {
    }

    public static String readResource(String path) throws URISyntaxException, IOException {
        URL url = ResourceReader.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.: " + path);
        }
        return new String(Files.readAllBytes(Path.of(url.toURI())));
    }
}
