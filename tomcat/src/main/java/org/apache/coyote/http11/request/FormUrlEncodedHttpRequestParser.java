package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeaders;

public class FormUrlEncodedHttpRequestParser extends HttpRequestParser {

    public FormUrlEncodedHttpRequestParser(final BufferedReader reader) {
        super(reader);
    }

    @Override
    public RequestBody parseRequestBody(final RequestHeaders requestHeaders) throws IOException {
        final int contentLength = Integer.parseInt(requestHeaders.getOrDefault("Content-Length", "0"));
        if (contentLength == 0) {
            return RequestBody.createEmptyBody();
        }
        final char[] buffer = new char[contentLength];
        readIntoBuffer(buffer, contentLength);
        final String rawHttpRequestBody = new String(buffer);
        return RequestBody.from(rawHttpRequestBody);
    }
}
