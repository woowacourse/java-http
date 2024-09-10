package com.techcourse.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.techcourse.exception.InvalidResourceException;

public class Resource {
    public static String read(String fileName) throws IOException {
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new InvalidResourceException("Cannot find resource with name: " + fileName);
        }
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private static URL findResource(String fileName) {
        return Resource.class.getClassLoader().getResource("static/" + fileName);
    }
}
