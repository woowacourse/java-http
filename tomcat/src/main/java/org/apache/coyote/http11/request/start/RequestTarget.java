package org.apache.coyote.http11.request.start;

public class RequestTarget {
    private final String origin;
    private final String extension;

    public RequestTarget(final String origin, final String extension) {
        this.origin = origin;
        this.extension = extension;
    }

    public static final RequestTarget from(final String requestTarget) {
        String extension;
        if (!requestTarget.contains("?")) {
            extension = requestTarget.substring(requestTarget.indexOf("."));
        } else extension = "?";
        return new RequestTarget(requestTarget, extension);
    }

    public String getOrigin() {
        return origin;
    }

    public String getExtension() {
        return extension;
    }
}
