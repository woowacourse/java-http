package org.apache.coyote.response;

import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.Protocol;

public class ResponseHeader {

    private final Protocol protocol;
    private final HttpStatus httpStatus;
    private final Map<String, String> headers;

    public ResponseHeader(Protocol protocol, HttpStatus httpStatus, Map<String, String> headers) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.headers = headers;
    }

    public String getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(protocol.getValue() + " " + httpStatus.getStatusCode() + " " + httpStatus.getStatus() + " ");
        for (Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuilder.append(key + ": " + value + " ");
        }
        return stringBuilder.toString();
    }
}
