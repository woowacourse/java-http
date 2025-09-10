package org.apache.coyote.http11.httpRequest;

import com.sun.net.httpserver.Request;
import java.util.Optional;

public class RequestLine {

    private final RequestMethod requestMethod;
    private final Uri uri;
    private final ProtocolVersion protocolVersion;

    private RequestLine(
            final RequestMethod requestMethod,
            final Uri uri,
            final ProtocolVersion protocolVersion
            ) {
        this.requestMethod = requestMethod;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine parse(final String requestLine) {
        final String[] tokens = requestLine.split(" ");

        final RequestMethod requestMethod = RequestMethod.parse(tokens[0]);
        final Uri uri = Uri.parse(tokens[1]);
        final ProtocolVersion protocolVersion = ProtocolVersion.parse(tokens[2]);

        return new RequestLine(requestMethod, uri, protocolVersion);
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public String getPath() {
        return this.uri.getPath();
    }

    public Optional<String> findParamsValue(final String name) {
        return this.uri.findParamsValue(name);
    }

    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }
}
