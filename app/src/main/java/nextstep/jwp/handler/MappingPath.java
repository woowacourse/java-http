package nextstep.jwp.handler;

import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.SourcePath;

import java.util.Objects;

public class MappingPath {
    private final HttpMethod httpMethod;
    private final SourcePath sourcePath;

    private MappingPath(HttpMethod httpMethod, SourcePath sourcePath) {
        this.httpMethod = httpMethod;
        this.sourcePath = sourcePath;
    }

    public static MappingPath of(HttpRequest httpRequest) {
        return MappingPath.of(httpRequest.requestLine().method(), httpRequest.sourcePath());
    }

    public static MappingPath of(HttpMethod httpMethod, SourcePath sourcePath) {
        return new MappingPath(httpMethod, sourcePath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappingPath that = (MappingPath) o;
        return httpMethod == that.httpMethod && Objects.equals(sourcePath, that.sourcePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, sourcePath);
    }
}
