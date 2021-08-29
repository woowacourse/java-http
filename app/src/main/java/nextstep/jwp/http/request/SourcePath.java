package nextstep.jwp.http.request;

import java.util.Objects;

public class SourcePath {

    private final String sourcePath;

    public SourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public static SourcePath of(String uriPath) {
        int index = uriPath.indexOf("?");
        if(index > 0){
            return new SourcePath(uriPath.substring(0, index));
        }
        return new SourcePath(uriPath);
    }

    public boolean isPath(String path) {
        return sourcePath.equalsIgnoreCase(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourcePath that = (SourcePath) o;
        return sourcePath.equalsIgnoreCase(that.sourcePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourcePath);
    }

    public String getValue() {
        return sourcePath;
    }
}
