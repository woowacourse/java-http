package org.apache.util;

import java.io.File;
import java.net.URL;

public class FileUtils {

    private FileUtils() {
    }

    public static File loadFile(String path) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(path + "가 존재하지 않습니다.");
        }
        return new File(resource.getFile());
    }
}
