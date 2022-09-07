package org.apache.coyote.http11.request;

import java.util.Optional;

public class RequestLine {

    private static final String FILE_EXTENSION_SIGN = ".";

    private final Method method;
    private final URI uri;
    private final String protocol;

    public RequestLine(Method method, URI uri, String protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public boolean isStaticFileRequest() {
        String path = uri.getPath();
        return path.contains(FILE_EXTENSION_SIGN);
    }

    public Optional<String> getExtension() {
        String path = uri.getPath();
        int index = path.indexOf(FILE_EXTENSION_SIGN);
        return Optional.of(path.substring(index + 1));
    }

    public boolean hasQueryParams() {
        return !uri.getQueryParams().isEmpty();
    }

    public Method getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", uri=" + uri +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}
