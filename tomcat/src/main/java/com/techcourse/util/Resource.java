package com.techcourse.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.techcourse.exception.InvalidResourceException;

public class Resource {
    private static final String BASE_PATH = "static/";
    private static final String FILE_NAME_DELIMITER = "?";
    private static final int FILE_NAME_START_POSITION = 1;
    private static final String DEFAULT_FILE_NAME = "hello.html";

    public static String getFileName(String uri) {
        int index = uri.indexOf(FILE_NAME_DELIMITER);
        String path = uri;
        if (index != -1) {
            path = path.substring(0, index);
        }
        String fileName = path.substring(FILE_NAME_START_POSITION);
        if (fileName.isEmpty()) {
            fileName = DEFAULT_FILE_NAME;
        }
        return fileName;
    }

    public static String read(String fileName) throws IOException {
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new InvalidResourceException("Cannot find resource with name: " + fileName);
        }
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private static URL findResource(String fileName) {
        return Resource.class.getClassLoader().getResource(BASE_PATH + fileName);
    }
}
