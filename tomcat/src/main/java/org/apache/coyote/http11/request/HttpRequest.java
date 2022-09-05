package org.apache.coyote.http11.request;

import java.util.Optional;

public class HttpRequest {

    private static final String REQUEST_LINE_SPLITTER = " ";
    private static final String FILE_EXTENSION_SIGN = ".";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final Method method;
    private final URI uri;
    private final String protocol;

    public HttpRequest(String requestLine) {
        String[] str = requestLine.split(REQUEST_LINE_SPLITTER);
        this.method = Method.from(str[METHOD_INDEX]);
        this.uri = new URI(str[URI_INDEX]);
        this.protocol = str[PROTOCOL_INDEX];
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
}
