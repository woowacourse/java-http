package com.techcourse.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class StaticResourceManager {

    private static final String STATIC_RESOURCE_PREFIX = "static";

    private StaticResourceManager() {
    }

    public static boolean isExist(String filePath) {
        URL resource = ClassLoader.getSystemClassLoader().getResource(STATIC_RESOURCE_PREFIX + filePath);
        try {
            File file = new File(Objects.requireNonNull(resource).toURI());
            return file.exists() && file.isFile();
        } catch (URISyntaxException | NullPointerException e) {
            return false;
        }
    }

    public static String read(String filePath) {
        URL resource = ClassLoader.getSystemClassLoader().getResource(STATIC_RESOURCE_PREFIX + filePath);

        try {
            Path path = Optional.ofNullable(resource)
                    .map(url -> {
                        try {
                            return url.toURI();
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(Path::of)
                    .orElseThrow(() -> new RuntimeException(filePath + " not found"));

            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
