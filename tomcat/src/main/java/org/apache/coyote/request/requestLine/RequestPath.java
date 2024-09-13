package org.apache.coyote.request.requestLine;

import java.util.Objects;
import org.apache.coyote.util.ContentType;

public class RequestPath {

    private static final String EXTENSION_SEPARATOR = ".";
    private static final String PARAM_SEPARATOR = "?";
    private static final String DEFAULT_PATH = "/";
    private static final int NO_PARAM_NUMBER = -1;
    private static final int START_INDEX = 0;

    private final String path;

    public RequestPath(String path) {
        if (path == null) {
            throw new NullPointerException("경로가 존재하지 않습니다.");
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getNoExtensionPath() {
        if (!path.contains(EXTENSION_SEPARATOR)) {
            return path;
        }
        int index = path.indexOf(EXTENSION_SEPARATOR);
        return path.substring(START_INDEX, index);
    }

    public boolean hasSamePath(String path) {
        return path.equals(getNoQueryPath());
    }

    public boolean isDefaultPath() {
        return DEFAULT_PATH.equals(path);
    }

    public boolean hasExtension() {
        return ContentType.hasExtension(path);
    }

    private String getNoQueryPath() {
        int index = path.indexOf(PARAM_SEPARATOR);

        if (index == NO_PARAM_NUMBER) {
            return path;
        }
        return path.substring(START_INDEX, index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestPath that = (RequestPath) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
