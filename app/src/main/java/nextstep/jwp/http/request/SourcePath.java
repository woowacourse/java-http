package nextstep.jwp.http.request;

import java.util.Objects;

public class SourcePath {

    private final String path;

    public SourcePath(String path) {
        this.path = path;
    }

    public static SourcePath of(String uriPath) {
        int index = uriPath.indexOf("?");
        if (index > 0) {
            return new SourcePath(uriPath.substring(0, index));
        }
        return new SourcePath(uriPath);
    }

    public String getValue() {
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
        SourcePath that = (SourcePath) o;
        return path.equalsIgnoreCase(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
