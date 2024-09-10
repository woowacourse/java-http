package org.apache.coyote.http11.response.header;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.HttpHeader;

public class ResponseHeaders {

    private final LinkedHashMap<HttpHeader, String> headers;

    public ResponseHeaders(LinkedHashMap<HttpHeader, String> headers) {
        this.headers = headers;
    }
}
