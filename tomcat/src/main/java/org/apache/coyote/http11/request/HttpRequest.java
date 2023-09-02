package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {
    private static final String CONTENT_LENGTH = "content-length";
    private final RequestURL requestURL;
    private final RequestHeaders requestHeaders;
    private final String requestBody;

    private HttpRequest(final RequestURL requestURL, final RequestHeaders requestHeaders, final String requestBody) {
        this.requestURL = requestURL;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final RequestURL requestURL = RequestURL.from(bufferedReader.readLine());
        final RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);
        final String requestBody = getRequestBody(requestHeaders, bufferedReader);
        return new HttpRequest(requestURL, requestHeaders, requestBody);
    }

    private static String getRequestBody(final RequestHeaders requestHeaders, final BufferedReader bufferedReader) throws IOException {
        final String contentLength = requestHeaders.getValue(CONTENT_LENGTH);

        if (contentLength == null) {
            return null;
        }

        char[] tmp = new char[Integer.parseInt(contentLength)];
        bufferedReader.read(tmp, 0, Integer.parseInt(contentLength));

        return String.copyValueOf(tmp);
    }

    public String getRequestResource() throws IOException {
        return requestURL.getResource();
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
