package org.apache.coyote.http11;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class TargetPath {

    private static final String DEFAULT_PAGE = "index";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String STATIC_RESOURCES_PATH = "static";

    private final String path;

    public TargetPath(String path) {
        this.path = path;
    }

    private boolean endsWithRoot(String path) {
        return path.endsWith("/");
    }

    private boolean hasExtensionIn(String path) {
        int lastSlash = path.lastIndexOf('/');
        String lastSubPath = path.substring(lastSlash + 1);
        if (lastSubPath.contains(".")) {
            return lastSubPath.lastIndexOf('.') > 0;
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public TargetPath autoComplete() {
        if (endsWithRoot(path)) {
            return new TargetPath(path + DEFAULT_PAGE + DEFAULT_EXTENSION);
        }
        if (!hasExtensionIn(path)) {
            return new TargetPath(path + DEFAULT_EXTENSION);
        }
        return new TargetPath(path);
    }

    public String getExtension() {
        int lastSlash = autoComplete().path.lastIndexOf('/');
        String lastSubPath = autoComplete().path.substring(lastSlash + 1);
        int extensionStart = lastSubPath.lastIndexOf('.');
        return lastSubPath.substring(extensionStart + 1);
    }

    public File asStaticFile() {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCES_PATH + autoComplete().path);
        if (resource == null) {
            return new File("/");
        }
        return new File(resource.getPath());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetPath that = (TargetPath) o;

        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
