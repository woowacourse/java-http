package org.apache.coyote.http11.request.element;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class Path {

    private final String path;

    private Path(String path) {
        this.path = path;
    }

    public static Path of(String path) {
        if (path == null) {
            return new Path("");
        }
        if (path.endsWith("/")) {
            return new Path(StringUtils.chop(path));
        }
        return new Path(path);
    }

    public String getPath() {
        return path;
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
