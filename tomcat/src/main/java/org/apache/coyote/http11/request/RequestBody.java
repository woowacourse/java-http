package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private final String requestBody;

    private RequestBody(final String requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody of(final String contentLength, final BufferedReader bufferedReader) throws IOException {
        if (contentLength == null) {
            return null;
        }

        char[] tmp = new char[Integer.parseInt(contentLength.trim())];
        bufferedReader.read(tmp, 0, Integer.parseInt(contentLength.trim()));

        return new RequestBody(String.copyValueOf(tmp));
    }

    public String getRequestBody() {
        return requestBody;
    }

    public int getContentLength() {
        return requestBody.length();
    }
}
