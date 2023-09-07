package org.apache.coyote.http11.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import javassist.NotFoundException;
import nextstep.jwp.controller.ViewController;

public class ResourceReader {

    public static final String STATIC_RESOURCE_DIRECTORY_NAME = "static";
    private static final Pattern RESOURCE_PATTERN_FILE_EXTENSION = Pattern.compile(".*\\.[^.]+");

    private ResourceReader() {
    }

    public static StaticResource read(final String uri) throws NotFoundException, IOException {
        final var path = findURL(uri)
                .map(url -> Paths.get(url.getPath()))
                .orElseThrow(() -> new NotFoundException("cannot find static resource for uri"));
        validateFileName(String.valueOf(path.getFileName()));

        return new StaticResource(
                new String(Files.readAllBytes(path)),
                Files.probeContentType(path)
        );
    }

    private static void validateFileName(final String fileName) throws NotFoundException {
        if (isNotStaticResourceURI(fileName)) {
            throw new NotFoundException("cannot find static resource for file name");
        }
    }

    private static boolean isNotStaticResourceURI(final String fileName) {
        return !RESOURCE_PATTERN_FILE_EXTENSION
                .matcher(fileName)
                .matches();
    }

    private static Optional<URL> findURL(final String uri) {
        final var classLoader = ViewController.class.getClassLoader();

        return Optional.ofNullable(classLoader.getResource(STATIC_RESOURCE_DIRECTORY_NAME + uri));
    }

}
