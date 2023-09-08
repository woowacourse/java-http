package org.apache.coyote.http11.request.requestLine.requestUri;

import java.util.Objects;

public class ResourcePath {

    public static final String ROOT_PATH = "/";

    private final String resourcePath;

    private ResourcePath(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public static ResourcePath from(final String resourcePath) {
        return new ResourcePath(resourcePath);
    }

    public boolean isRootPath() {
        return resourcePath.equals(ROOT_PATH);
    }

    public boolean is(final String targetUrl) {
        return resourcePath.equals(targetUrl);
    }

    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResourcePath that = (ResourcePath) o;
        return Objects.equals(resourcePath, that.resourcePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourcePath);
    }

    @Override
    public String toString() {
        return "ResourcePath{" +
                "resourcePath='" + resourcePath + '\'' +
                '}';
    }
}
