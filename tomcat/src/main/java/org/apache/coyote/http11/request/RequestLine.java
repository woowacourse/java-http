package org.apache.coyote.http11.request;

public class RequestLine {

    private final String requestMethod;
    private final String requestUrl;
    private final String requestProtocol;

    public RequestLine(final String requestMethod, final String requestUrl, final String requestProtocol) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.requestProtocol = requestProtocol;
    }

    public static RequestLine from(final String line) {
        String[] requestParts = line.split(" ");
        if(requestParts.length != 3) {
            throw new IllegalArgumentException(); //todo : 유효하지 않은 헤더 예외처리
        }
        return new RequestLine(requestParts[0], requestParts[1], requestParts[2]);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestProtocol() {
        return requestProtocol;
    }
}
