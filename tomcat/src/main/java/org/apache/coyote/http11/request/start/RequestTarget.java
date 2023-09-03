package org.apache.coyote.http11.request.start;

public class RequestTarget {
    private final String origin;
    private final String resources;
    private final HttpExtension extension;

    public RequestTarget(final String origin, final HttpExtension extension, final String resources) {
        this.origin = origin;
        this.extension = extension;
        this.resources = resources;
    }

    public static RequestTarget from(final String requestTarget) {
        final HttpExtension extension = HttpExtension.from(requestTarget);
        final String resources = extension.findResources(requestTarget);
        return new RequestTarget(requestTarget, extension, resources);
    }

    public String getOrigin() {
        return origin;
    }

    public String getResources() {
        return resources;
    }

    public HttpExtension getExtension() {
        return extension;
    }
    public String getExtensionName() {
        return extension.getExtension();
    }
}
