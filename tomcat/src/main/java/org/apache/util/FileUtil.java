package org.apache.util;

import java.io.File;
import java.net.URL;

public class FileUtil {

    private FileUtil() {
    }

    public static File loadFile(String path) {
        URL resource = findResource(path);
        return toFile(resource);
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
