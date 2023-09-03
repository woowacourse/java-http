package org.apache.coyote.http11;

import java.net.URISyntaxException;

public class RequestLine {

    private final String httpMethod;
    private final RequestUri requestUri;
    private final String httpVersion;

    private RequestLine(String httpMethod, RequestUri requestUri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] requestLineParts = requestLine.split(" ");
        RequestUri requestUri = RequestUri.from(requestLineParts[1]);

        return new RequestLine(requestLineParts[0], requestUri, requestLineParts[2]);
    }

    public HttpResponse extractHttpResponse() {
        try {
            return requestUri.extractHttpResponse();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("해당 파일을 찾을 수 없습니다.");
        }
    }

    public boolean isQueryStringExisted() {
        return requestUri.isQueryStringExisted();
    }

    public String findQueryStringValue(String key) {
        return requestUri.findQueryStringValue(key);
    }

}
