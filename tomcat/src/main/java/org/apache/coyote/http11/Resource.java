package org.apache.coyote.http11;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.http11.request.HttpRequest;

public class Resource {

    private static final String STATIC_PATH_PREFIX = "static";

    private final String value;

    public Resource(String value) {
        this.value = value;
    }

    public static Resource from(HttpRequest request) {
        return from(request.getPath());
    }

    public static Resource from(String path) {
        return new Resource(readFileAt(path));
    }

    private static String readFileAt(String path) {
        final URL resource = Resource.class.getClassLoader().getResource(STATIC_PATH_PREFIX + path);
        try {
            return Files.readString(
                getFilePathFrom(resource));
        } catch (Exception e) {
            return readFileAt("/404.html");
        }
    }

    private static Path getFilePathFrom(URL resource) throws NullPointerException {
        return new File(Objects.requireNonNull(resource)
            .getFile()).toPath();
    }

    public String getValue() {
        return value;
    }
}
