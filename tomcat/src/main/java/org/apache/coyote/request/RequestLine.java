package org.apache.coyote.request;

import org.apache.coyote.Protocol;

public class RequestLine {
    private static final String SPLIT_DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final Method method;
    private final Uri uri;
    private final Protocol protocol;

    private RequestLine(final Method method, final Uri uri, final Protocol protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public static RequestLine from(final String line) {
        final String[] requestUri = line.split(SPLIT_DELIMITER);
        final String method = requestUri[METHOD_INDEX];
        final String uri = requestUri[URI_INDEX];
        final String protocol = requestUri[PROTOCOL_INDEX];

        return new RequestLine(
                Method.getMethod(method),
                Uri.from(uri),
                Protocol.getProtocol(protocol)
        );
    }

    public Method getMethod() {
        return method;
    }

    public Uri getUri() {
        return uri;
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
