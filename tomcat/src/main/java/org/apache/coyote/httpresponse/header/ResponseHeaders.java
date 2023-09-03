package org.apache.coyote.httpresponse.header;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<ResponseHeaderType, ResponseHeader> headers;

    private ResponseHeaders(Map<ResponseHeaderType, ResponseHeader> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(final Path path) {
        final Map<ResponseHeaderType, ResponseHeader> headers = new HashMap<>();
        headers.put(ResponseHeaderType.CONTENT_TYPE, ContentTypeHeader.from(path));
        headers.put(ResponseHeaderType.CONTENT_LENGTH, new ContentLengthHeader(path));
        return new ResponseHeaders(headers);
    }
}
