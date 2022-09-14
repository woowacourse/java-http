package org.apache.coyote.http11.httpmessage.request.requestbody;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {
    private final String requestBody;

    public RequestBody(final String requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody from(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);

        return new RequestBody(String.valueOf(body));
    }

    public String getRequestBody() {
        return requestBody;
    }
}
