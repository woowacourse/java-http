package org.apache.coyote.httprequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final String contents;

    private RequestBody(final String contents) {
        this.contents = contents;
    }

    public static RequestBody from(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        log.debug("Request body:");
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);
        return new RequestBody(requestBody);
    }
}
