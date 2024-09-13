package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.HttpHeader;

import java.util.stream.Collectors;

public class ResponseHeaders extends Headers {

    public ResponseHeaders() {
        super();
    }

    public void put(HttpHeader httpHeader, String header) {
        getHeaders().put(httpHeader.getName(), header);
    }

    public String getMessage() {
        return getHeaders().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
