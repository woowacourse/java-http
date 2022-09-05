package org.apache.coyote.http11.request;

import java.util.Optional;

public class HttpRequest {

    private static final String REQUEST_LINE_SPLITTER = " ";
    private static final String FILE_EXTENSION_SIGN = ".";
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int REQUEST_PROTOCOL_INDEX = 2;

    private final String requestMethod;
    private final URI uri;
    private final String protocol;

    public HttpRequest(String requestLine) {
        String[] str = requestLine.split(REQUEST_LINE_SPLITTER);
        this.requestMethod = str[REQUEST_METHOD_INDEX];
        this.uri = new URI(str[REQUEST_URI_INDEX]);
        this.protocol = str[REQUEST_PROTOCOL_INDEX];
    }

    public boolean isStaticFileRequest() {
        return uri.getPath().contains(FILE_EXTENSION_SIGN);
    }

    public Optional<String> getExtension() {
        String path = uri.getPath();
        int index = path.indexOf(FILE_EXTENSION_SIGN);
        return Optional.of(path.substring(index + 1));
    }

    public String getProtocol() {
        return protocol;
    }

    public URI getUri() {
        return uri;
    }
}
