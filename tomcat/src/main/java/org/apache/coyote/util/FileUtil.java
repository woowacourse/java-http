package org.apache.coyote.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtil {

    private FileUtil() {
    }

    public static String readAllBytes(String uri) {
        try {
            URL resource = FileUtil.class.getClassLoader().getResource("static" + uri);
            Path filePath = new File(Objects.requireNonNull(resource).getFile()).toPath();
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean existResource(String uri) {
        URL resource = FileUtil.class.getClassLoader().getResource("static" + uri);
        return resource != null;
    }
}
