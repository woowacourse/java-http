package org.apache.coyote.http11.request;

import org.apache.coyote.http11.exception.InvalidRequestException;

public class RequestLine {

    private final RequestMethod requestMethod;
    private final String requestUrl;
    private final String requestProtocol;

    public RequestLine(final RequestMethod requestMethod, final String requestUrl, final String requestProtocol) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.requestProtocol = requestProtocol;
    }

    public static RequestLine from(final String line) {
        String[] requestParts = line.split(" ");
        if (requestParts.length != 3) {
            throw new InvalidRequestException();
        }
        RequestMethod requestMethod = RequestMethod.getRequestMethod(requestParts[0]);
        return new RequestLine(requestMethod, requestParts[1], requestParts[2]);
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestURI() {
        return requestUrl;
    }

    public String getRequestProtocol() {
        return requestProtocol;
    }
}
