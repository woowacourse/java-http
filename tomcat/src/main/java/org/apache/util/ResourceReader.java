package org.apache.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceReader {

    private ResourceReader() {
    }

    public static String readContent(String source) throws IOException {
        URL url = readUrl(source);
        Path path = new File(url.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }

    private static URL readUrl(String path) {
        URL url = ResourceReader.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.: " + path);
        }
        return url;
    }

    public static String readeContentType(String source) throws IOException {
        URL url = readUrl(source);
        Path path = new File(url.getPath()).toPath();
        return Files.probeContentType(path);
    }
}
