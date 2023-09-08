package org.apache.coyote.http11.request.start;

public class RequestTarget {
    private final String path;
    private final HttpExtension extension;

    public RequestTarget(final HttpExtension extension, final String path) {
        this.extension = extension;
        this.path = path;
    }

    public static RequestTarget from(final String requestTarget) {
        final HttpExtension extension = HttpExtension.from(requestTarget);
        final String resources = extension.findResources(requestTarget);
        return new RequestTarget(extension, resources);
    }

    public String getPath() {
        return path;
    }

    public HttpExtension getExtension() {
        return extension;
    }

    public String getExtensionName() {
        return extension.getExtension();
    }
}
