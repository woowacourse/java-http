package org.apache.coyote.http11.handler.support;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private FileReader() {
    }

    public static String getFile(final String path, final Class<?> classes) {
        try {
            URL resource = classes.getResource("/static" + path);
            return Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalArgumentException("파일을 불러올 수 없습니다.");
        }
    }
}
