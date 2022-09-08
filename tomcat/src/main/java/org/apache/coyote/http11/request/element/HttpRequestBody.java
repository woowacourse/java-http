package org.apache.coyote.http11.request.element;

import static org.apache.coyote.Constants.CRLF;

public class HttpRequestBody {

    private final String bodyContext;

    public HttpRequestBody(final String bodyContext) {
        this.bodyContext = bodyContext;
    }

    public static HttpRequestBody of(String request) {
        String[] split = request.split(CRLF + CRLF);
        String bodyContext = split[1];
        return new HttpRequestBody(bodyContext);
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody("");
    }

    public String getBodyContext() {
        return bodyContext;
    }

    @Override
    public String toString() {
        return "HttpRequestBody{" +
                "bodyContext='" + bodyContext + '\'' +
                '}';
    }
}
