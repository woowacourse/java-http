package org.apache.coyote.http11.request;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.isNull;

public class Location {

    private static final String HTTP_HEADER_DELIMITER = " ";
    private static final int LOCATION_INDEX = 1;
    private static final String BASE_PATH = "static";
    private static final String REGEX_FOR_EXTENSION = "\\.[^.]+$";
    private static final String ROOT = "/";
    private static final String EMPTY = "";

    private final Path path;
    private final ContentType contentType;

    public Location(final Path path, final ContentType contentType) {
        this.path = path;
        this.contentType = contentType;
    }

    public static Location from(final String header) {
        final String location = header.split(HTTP_HEADER_DELIMITER)[LOCATION_INDEX];

        final ContentType contentType = ContentType.from(location);
        final Path path = getPath(location);

        return new Location(path, contentType);
    }

    private static Path getPath(final String location) {
        final ContentType contentType = ContentType.from(location);

        final ClassLoader classLoader = Location.class.getClassLoader();
        final String locationWithoutExtension = location.replaceAll(REGEX_FOR_EXTENSION, EMPTY);
        final URL resource = classLoader.getResource(BASE_PATH +
                locationWithoutExtension +
                "." +
                contentType.getExtension());

        if (isNull(resource)) {
            return Paths.get(ROOT);
        }
        return Paths.get(resource.getPath());
    }

    public boolean is(final String path) {
        return this.path.endsWith(path);
    }

    public boolean isRoot() {
        return is(ROOT);
    }

    public Path getPath() {
        return path;
    }

    public String contentTypeHeader() {
        return contentType.toHeader();
    }
}
