package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.request.query.QueryParams;

public class HttpRequestBody {

    private final String requestBody;

    private HttpRequestBody(final String requestBody) {
        this.requestBody = requestBody;
    }

    public static HttpRequestBody from(String requestBody) {
        return new HttpRequestBody(requestBody);
    }

    public static HttpRequestBody readRequestBody(final BufferedReader bufferedReader, final HttpHeader httpHeader)
            throws IOException {
        final String contentLengthHeader = httpHeader.getContentLength();
        if (contentLengthHeader == null) {
            return new HttpRequestBody("");
        }

        final int contentLength = Integer.parseInt(contentLengthHeader.trim());
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new HttpRequestBody(new String(buffer));
    }

    public QueryParams getBodyWithQueryParamForm() {
        return QueryParams.from(requestBody);
    }
}
