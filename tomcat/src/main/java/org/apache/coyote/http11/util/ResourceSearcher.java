package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceSearcher {

    private static final String STATIC_PATH = "static";

    public boolean isFile(final String fileName) {
        return fileName.contains(".");
    }

    public String loadContent(final String fileName) {
        final URL path = getClass().getClassLoader().getResource(STATIC_PATH + fileName);
        try {
            return new String(Files.readAllBytes(new File(path.getFile()).toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("잘못된 경로입니다. [%s]", fileName));
        }
    }
}
