package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {
    private final RequestURL requestURL;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;
    private static final String CONTENT_LENGTH = "Content-Length";

    private HttpRequest(final RequestURL requestURL, final RequestHeaders requestHeaders, final RequestBody requestBody) {
        this.requestURL = requestURL;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        final RequestURL requestURL = RequestURL.from(br);
        final RequestHeaders requestHeaders = RequestHeaders.from(br);
        final RequestBody requestBody = RequestBody.of(requestHeaders.getValue(CONTENT_LENGTH), br);

        return new HttpRequest(requestURL, requestHeaders, requestBody);
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestURL getRequestURL() {
        return requestURL;
    }

    public String getRequestBody() {
        return requestBody.getRequestBody();
    }
}
