package org.apache.coyote.http11.request.element;

import java.util.List;
import java.util.Objects;

public class Path {

    private static final String PATH_REGEX = "\\?";

    private final String path;

    private Path(String path) {
        this.path = path;
    }

    public static Path of(String uri) {
        return new Path(uri.split(PATH_REGEX)[0]);
    }

    public String getPath() {
        return path;
    }

    public boolean same(String path) {
        return this.path.equals(path);
    }

    public boolean contains(List<String> paths) {
        return paths.contains(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Path path1 = (Path) o;
        return Objects.equals(getPath(), path1.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }
}
