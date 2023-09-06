package org.apache.coyote.http11.response.util;

import javassist.NotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResourceFinder {

    private static final String DEFAULT_PATH = "static";

    private ResourceFinder() {
    }

    public static String getStaticResource(String path) throws IOException, URISyntaxException, NotFoundException {
        try {
            URI resource = Objects.requireNonNull(Thread.currentThread()
                            .getContextClassLoader()
                            .getResource(DEFAULT_PATH + path))
                    .toURI();
            return new String(Files.readAllBytes(Path.of(resource)));
        } catch (NullPointerException e) {
            throw new NotFoundException("파일이 없습니다. " + path);
        }
    }
}
