package org.apache.coyote.http11.request;

public class HttpRequestBody {
    private final String httpRequestBodys;

    private HttpRequestBody(final String httpRequestBodys) {
        this.httpRequestBodys = httpRequestBodys;
    }

    public static HttpRequestBody from(final String httpRequestBody) {
        return new HttpRequestBody(httpRequestBody);
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(null);
    }

    public String getHttpRequestBodys() {
        return httpRequestBodys;
    }
}
