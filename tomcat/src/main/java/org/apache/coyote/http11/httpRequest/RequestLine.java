package org.apache.coyote.http11.httpRequest;

import java.util.Map;

public class RequestLine {

    private final RequestMethod requestMethod;
    private final Uri uri;

    private RequestLine(
            final RequestMethod requestMethod,
            final Uri uri
    ) {
        this.requestMethod = requestMethod;
        this.uri = uri;
    }

    public static RequestLine parse(final String requestLine) {
        final String[] tokens = requestLine.split(" ");

        final RequestMethod requestMethod = RequestMethod.parse(tokens[0]);
        final Uri uri = Uri.parse(tokens[1]);

        return new RequestLine(requestMethod, uri);
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public String getPath() {
        return this.uri.getPath();
    }

    public Map<String, String> getParams() {
        return this.uri.getParams();
    }
}
