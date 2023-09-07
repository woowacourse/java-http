package org.apache.coyote.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class FileUtil {

    private FileUtil() {
    }

    public static String readStaticFile(String uri) {
        URL resource = FileUtil.class
            .getClassLoader()
            .getResource(uri);
        try {
            URL url = Objects.requireNonNull(resource, "파일을 찾을 수 없습니다. 파일: " + uri);
            return new String(
                Files.readAllBytes(new File(url.getFile()).toPath())
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
