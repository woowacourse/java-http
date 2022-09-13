package org.apache.coyote.http11.request;

import java.util.Objects;

public class Path {

    private static final int EXCLUDE_SLASH_INDEX = 1;
    private static final int NOT_EXIST_QUERY_PARAMETER_CHARACTER = -1;

    private final String value;

    private Path(String uri) {
        this.value = uri;
    }

    public static Path of(String uri) {
        int queryParameterIndex = uri.indexOf("?");
        if (queryParameterIndex != NOT_EXIST_QUERY_PARAMETER_CHARACTER) {
            return new Path(uri.substring(0, queryParameterIndex));
        }
        return new Path(uri);
    }

    public static Path fromPath(String path) {
        return new Path(path);
    }

    public boolean isFileRequest() {
        return value.contains(".");
    }

    public String getFileName() {
        return value.substring(EXCLUDE_SLASH_INDEX);
    }

    public boolean checkRequest(String path) {
        return value.equals(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Path path = (Path) o;

        return Objects.equals(value, path.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
