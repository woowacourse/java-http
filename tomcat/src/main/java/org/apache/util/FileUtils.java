package org.apache.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileUtils {

    private static final String STATIC_RESOURCE_PATH = "static";

    private FileUtils() {
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1);
    }

    public static String readFile(final String fileName) throws IOException {
        URL resource = FileUtils.class.getClassLoader().getResource(STATIC_RESOURCE_PATH + fileName);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
