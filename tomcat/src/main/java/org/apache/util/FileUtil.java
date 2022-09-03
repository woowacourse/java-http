package org.apache.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileUtil {

    private FileUtil() {
    }

    public static String loadFile(String path) {
        try {
            URL resource = findResource(path);
            File file = toFile(resource);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException(path + "가 존재하지 않습니다.");
        }
    }

    private static URL findResource(String path) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(path + "가 존재하지 않습니다.");
        }
        return resource;
    }

    private static File toFile(URL resource) {
        return new File(resource.getFile());
    }
}
